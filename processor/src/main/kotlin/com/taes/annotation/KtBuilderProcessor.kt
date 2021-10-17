package com.taes.annotation

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

class KtBuilderProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.taes.annotation.KtBuilder")
        val result = symbols.filter { !it.validate() }.toList()
        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(BuilderVisitor(), Unit) }
        return result
    }

    inner class BuilderVisitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            classDeclaration.primaryConstructor!!.accept(this, data)
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            val parent = function.parentDeclaration as KSClassDeclaration
            val parentClassName = parent.simpleName.asString()
            val packageName = parent.containingFile!!.packageName.asString()
            val className = "${parentClassName}Builder"

            var propertySpecList = ArrayList<PropertySpec>()
            var funSpecList = ArrayList<FunSpec>()

            function.parameters.forEach {
                val propertyName = it.name!!.asString()
                val typeNameBuilder = StringBuilder(it.type.resolve().declaration.qualifiedName?.asString() ?: "<ERROR>")
                val typeArgs = it.type.element!!.typeArguments
                if (typeArgs.isNotEmpty()) {
                    typeNameBuilder.append("<")
                    typeNameBuilder.append(
                        typeArgs.map {
                            val type = it.type?.resolve()
                            "${it.variance.label} ${type?.declaration?.qualifiedName?.asString() ?: "ERROR"}" +
                                    if (type?.nullability == Nullability.NULLABLE) "?" else ""
                        }.joinToString(", ")
                    )
                    typeNameBuilder.append(">")
                }

                val typeName = ClassName.bestGuess(typeNameBuilder.toString()).copy(nullable = true)

                propertySpecList.add(
                    PropertySpec.builder(propertyName, typeName)
                        .addModifiers(KModifier.PRIVATE)
                        .mutable(true)
                        .initializer("null")
                        .build()
                )

                funSpecList.add(
                    FunSpec.builder(propertyName)
                        .addParameter(propertyName, typeName)
                        .returns(ClassName(packageName, className))
                        .addStatement(
                            """
                                this.$propertyName = $propertyName
                                return this
                            """.trimIndent()
                        )
                        .build()
                )
            }

            val builder = StringBuilder()
            funSpecList.add(
                FunSpec.builder("build")
                    .returns(ClassName(packageName, parentClassName))
                    .addStatement(
                        StringBuilder()
                            .append("return ${parentClassName}(")
                            .append(function.parameters.map{
                                "${it.name!!.asString()}!!"
                            }.joinToString(", "))
                            .append(")")
                            .toString()
                    )
                    .build()
            )

            val classSpec = TypeSpec.classBuilder(className)
                .addProperties(propertySpecList)
                .addFunctions(funSpecList)
                .build()


            val file = codeGenerator.createNewFile(
                Dependencies(true, function.containingFile!!), packageName, className
            )

            val fileSpec = FileSpec.builder(packageName, className)
                .addType(classSpec)
                .build()

            OutputStreamWriter(file, StandardCharsets.UTF_8)
                .use { fileSpec.writeTo(it) }
        }
    }

}

class KtBuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(
        env: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return KtBuilderProcessor(env.codeGenerator, env.logger)
    }
}