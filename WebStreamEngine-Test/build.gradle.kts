import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.distsDirectory
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.8.0-RC2"
}

group = "webstreamengine.test"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":WebStreamEngine-Client"))
    implementation("com.badlogicgames.gdx:gdx:1.11.0")
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

tasks.getByName<Jar>("jar") {
    destinationDirectory.set(file("$rootDir\\assets"))
}