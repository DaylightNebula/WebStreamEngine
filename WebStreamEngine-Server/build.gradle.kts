import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.8.0-RC2"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

group = "webstreamengine.server"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation(kotlin("stdlib-jdk8"))

    api(project(":WebStreamEngine-Core"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("WebStreamEngine-Server")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "webstreamengine.server.ServerMainKt"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

//tasks.withType<Jar> {
//    manifest {
//        attributes["Class-Path"] = configurations.compileClasspath.toString()
//        attributes["Main-Class"] = "webstreamengine.server.ServerMainKt"
//    }
//}