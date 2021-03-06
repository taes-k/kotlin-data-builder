plugins {
    kotlin("jvm") version "1.5.31"
}

group = "com.taes"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kspVersion = "1.5.31-1.0.0"
dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.squareup:kotlinpoet:1.10.1")
}