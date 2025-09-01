/*
 * AeGenesis Core - Settings Configuration
 * This file configures the build settings and project structure for the AeGenesis Android project.
 */

// Enable Gradle features
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

// Plugin Management
pluginManagement {
    // Include build-logic for convention plugins
    includeBuild("build-logic")
    
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jitpack.io")
    }
    
    // Apply the foojay-resolver plugin for JDK management
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    }
}

// Dependency Resolution Management
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jitpack.io")
        maven("https://api.xposed.info/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        // LSPosed repository
        maven("https://repo.lsposed.org/maven")
        // Yuki API repository
        maven("https://s01.oss.sonatype.org/content/repositories/releases/")
    }
    
    // Version catalog is automatically loaded from gradle/libs.versions.toml
}

// Root project configuration
rootProject.name = "MemoriaOs"

// Include all modules
include(":app")
include(":core-module")
include(":feature-module")
include(":datavein-oracle-native")
include(":lsposed-module")
include(":collab-canvas")
include(":colorblendr")
include(":romtools")
include(":utilities")
include(":list")
include(":oracle-drive-integration")
include(":secure-comm")
