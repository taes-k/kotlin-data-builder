package com.taes.annotation

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class KtBuilderProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        // TODO:
        // println("getSupportedAnnotationTypes")
        return mutableSetOf(KtBuilder::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        // TODO:
        return SourceVersion.latest()
    }
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {

        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "is interesting.")
        if (roundEnv == null) {
            return false;
        }


//        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        roundEnv.getElementsAnnotatedWith(KtBuilder::class.java)
            .forEach {
//            val variableAsElement = processingEnv.typeUtils.asElement(variable.asType())
//            val fieldsInArgument = ElementFilter.fieldsIn(variableAsElement.enclosedElements)
//            val annotationArgs = method.getAnnotation(BindField::class.java).viewIds

                builderVisitor(it);

                val parentName = it.simpleName.toString()
                val packageName = processingEnv.elementUtils.getPackageOf(it).toString()
                val fileName = "${parentName}Builder"

                val classBuilder = TypeSpec.classBuilder(fileName)

                for (enclosed in it.enclosedElements) {
                    if (enclosed.kind == ElementKind.FIELD) {
                        classBuilder.addProperty(
                            PropertySpec.builder(
                                enclosed.simpleName.toString(),
                                enclosed.asType().asTypeName(),
                                KModifier.PRIVATE
                            )
                                .initializer("null")
                                .build()
                        )
                    }
                }

                val file = FileSpec.builder(packageName, fileName)
                    .addType(classBuilder.build())
                    .build()

                file.writeTo(File(kaptKotlinGeneratedDir, "$fileName.kt"))
//                file.writeTo(File("build/generated/", "$fileName.kt"))
            }

        return true
    }

    private fun builderVisitor(element: Element): Boolean {

        val parentName = element.simpleName.toString()
        return true
    }
}

//fun OutputStream.appendText(str: String) {
//    this.write(str.toByteArray())
//}
//
//class KtBuilderProcessor(
//    val codeGenerator: CodeGenerator,
//    val logger: KSPLogger
//) : SymbolProcessor {
//    override fun process(resolver: Resolver): List<KSAnnotated> {
//        val symbols = resolver.getSymbolsWithAnnotation("com.taes.annotation.KtBuilder")
//        val result = symbols.filter { !it.validate() }.toList()
//        symbols.filter { it is KSClassDeclaration && it.validate() }
//            .forEach { it.accept(BuilderVisitor(), Unit) }
//        return result
//    }
//
//    inner class BuilderVisitor : KSVisitorVoid() {
//        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
//            classDeclaration.primaryConstructor!!.accept(this, data)
//        }
//
//        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
//            val parent = function.parentDeclaration as KSClassDeclaration
//            val parentClassName = parent.simpleName.asString()
//            val packageName = parent.containingFile!!.packageName.asString()
//            val className = "${parentClassName}Builder"
//
//            val file = codeGenerator.createNewFile(Dependencies(true, function.containingFile!!), packageName, className)
//
//            file.appendText("package ${packageName}\n\n")
//            file.appendText("class ${className}{\n")
//
//            function.parameters.forEach {
//                val name = it.name!!.asString()
//                val typeName = StringBuilder(it.type.resolve().declaration.qualifiedName?.asString() ?: "<ERROR>")
//                val typeArgs = it.type.element!!.typeArguments
//                if (it.type.element!!.typeArguments.isNotEmpty()) {
//                    typeName.append("<")
//                    typeName.append(
//                        typeArgs.map {
//                            val type = it.type?.resolve()
//                            "${it.variance.label} ${type?.declaration?.qualifiedName?.asString() ?: "ERROR"}" +
//                                    if (type?.nullability == Nullability.NULLABLE) "?" else ""
//                        }.joinToString(", ")
//                    )
//                    typeName.append(">")
//                }
//                file.appendText("    private var $name: $typeName? = null\n")
//                file.appendText("    fun ${name}($name: $typeName): $className {\n")
//                file.appendText("        this.$name = $name\n")
//                file.appendText("        return this\n")
//                file.appendText("    }\n\n")
//            }
//            file.appendText("    fun build(): ${parent.qualifiedName!!.asString()} {\n")
//            file.appendText("        return ${parent.qualifiedName!!.asString()}(")
//            file.appendText(
//                function.parameters.map {
//                    "${it.name!!.asString()}!!"
//                }.joinToString(", ")
//            )
//            file.appendText(")\n")
//            file.appendText("    }\n")
//            file.appendText("}\n")
//            file.close()
//        }
//    }
//
//}
//
//class KtBuilderProcessorProvider : SymbolProcessorProvider {
//    override fun create(
//        env: SymbolProcessorEnvironment
//    ): SymbolProcessor {
//        return KtBuilderProcessor(env.codeGenerator, env.logger)
//    }
//}