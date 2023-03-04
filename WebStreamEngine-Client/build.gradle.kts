import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.8.0-RC2"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

group = "webstreamengine.client"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    implementation(kotlin("stdlib-jdk8"))
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // libgdx stuffs
    implementation("com.badlogicgames.gdx:gdx:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-platform:1.11.0:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:1.11.0:natives-desktop")

    // GLTF/GLB file loader stuffs
    implementation("com.github.mgsx-dev.gdx-gltf:core:2.1.0")
    implementation("com.github.mgsx-dev.gdx-gltf:gltf:2.1.0:sources")

    // bullet stuffs
    implementation("com.badlogicgames.gdx:gdx-bullet:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-bullet-platform:1.11.0:natives-desktop")

    implementation("org.json:json:20220924")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("io.ktor:ktor-network:2.2.3")
    implementation("io.ktor:ktor-network-tls:2.2.3")
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
        archiveBaseName.set("WebStreamEngine-Client")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "webstreamengine.client.ClientMainKt"))
        }
    }
}
//
//tasks {
//    build {
//        dependsOn(shadowJar)
//    }
//}
