import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories(RepositoryHandler::mavenCentral)

dependencies {
    implementation("org.anglur:joglext:1.0.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.time.ExperimentalTime")
}

application {
    mainClass.set("ApplicationKt")
}
