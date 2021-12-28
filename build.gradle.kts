import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories(RepositoryHandler::mavenCentral)

dependencies {
    implementation("org.anglur:joglext:1.0.3")
}

with(tasks) {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.time.ExperimentalTime")
    }
    withType<ShadowJar> {
        manifest {
            attributes(Pair("Main-Class", "ApplicationKt"))
        }
    }
}

application {
    mainClass.set("ApplicationKt")
}
