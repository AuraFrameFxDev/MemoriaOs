/*
 * Build logic for AeGenesis Gradle plugins
 * This file defines the classpath for convention plugins
 */

plugins {
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.1"
    id("org.jetbrains.kotlin.jvm") version "2.2.20-RC"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.aura.genesis"
version = "1.0.0"

// Configure Java toolchain consistently
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        freeCompilerArgs.addAll(
            "-Xjvm-default=all",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xskip-prerelease-check"
        )
    }
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

// Version definitions for build-logic plugins (consistent with main project)
val kotlinVersion = "2.2.20-RC"
val agpVersion = "9.0.0-alpha02" // Match main project AGP version
val hiltVersion = "2.50"
val kspVersion = "2.2.20-RC-2.0.2"
val composeVersion = "1.6.0"

// Dependencies for our convention plugins
dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    
    // Android Gradle Plugin - matching main project version
    implementation("com.android.tools.build:gradle:$agpVersion")
    
    // KSP
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kspVersion")
    
    // Hilt
    implementation("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.0.0-Beta4")
}

gradlePlugin {
    plugins {
        create("compose-conventions") {
            id = "buildlogic.compose-conventions"
            implementationClass = "BuildLogicComposeConventionPlugin"
            displayName = "Compose Conventions"
            description = "Configures Compose for Android projects"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi"
        )
    }
}

// Configure publishing
publishing {
    repositories {
        maven {
            name = "local"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}
