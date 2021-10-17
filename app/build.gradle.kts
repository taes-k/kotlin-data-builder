plugins {
//    id("com.google.devtools.ksp") version "1.5.30-1.0.0"
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
}

group = "com.taes"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly(project(":processor"))
    kapt(project(":processor"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/source/kaptKotlin/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/source/kaptKotlin/test/kotlin")
    }
}


tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
//
//    withType<DependencyUpdatesTask> {
//        rejectVersionIf {
//            isNonStable(candidate.version)
//        }
//        gradleReleaseChannel = CURRENT.id
//    }

//    named<Wrapper>("wrapper") {
//        gradleVersion = "7.2"
//        distributionType = ALL
//    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any {
        version.toUpperCase()
            .contains(it)
    }
    val unstableKeyword =
        listOf("""M\d+""").any { version.toUpperCase().contains(it.toRegex()) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = (stableKeyword && !unstableKeyword) || regex.matches(version)
    return isStable.not()
}