//val kspVersion: String by project

plugins {
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
    implementation ("com.squareup:kotlinpoet:1.10.1")

//    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.google.auto.service:auto-service:1.0")
    kapt("com.google.auto.service:auto-service:1.0")
}