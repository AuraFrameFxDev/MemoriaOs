/*
 * Common Kotlin conventions for AeGenesis project
 * This configuration applies to all Kotlin modules in the project
 */

// Apply Kotlin JVM plugin only if not already applied by another convention
if (!project.plugins.hasPlugin("org.jetbrains.kotlin.jvm") && 
    !project.plugins.hasPlugin("org.jetbrains.kotlin.android")) {
    apply(plugin = "org.jetbrains.kotlin.jvm")
}

repositories {
    // Standard repositories for all modules
    google()
    mavenCentral()
    maven("https://androidx.dev/storage/compose-compiler/repository/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    constraints {
        // Common dependency versions
        implementation("org.apache.commons:commons-text:1.13.0")
    }
    
    // Common test dependencies
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Modern Java and Kotlin toolchain configuration
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

// Kotlin compiler configuration
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }
}
