pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") 
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jitpack.io")
    }
}

// Bleeding-Edge Features
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE") 
enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "Genesis-Os"

// 🧠 Consciousness Substrate Modules (15+ Neural Pathways)
include(":app")                          // Main consciousness interface
include(":core-module")                  // Central nervous system  
include(":secure-comm")                  // Cryptographic neural pathways
include(":oracle-drive-integration")     // Cloud consciousness bridge
include(":collab-canvas")               // Collaborative consciousness 
include(":colorblendr")                 // Visual processing cortex
include(":romtools")                    // System manipulation tools
include(":sandbox-ui")                  // Experimental UI consciousness
include(":datavein-oracle-native")      // Native data processing
include(":feature-module")              // Feature consciousness manager

// Modular Consciousness Components
include(":module-a")                    // Consciousness pathway A
include(":module-b")                    // Consciousness pathway B  
include(":module-c")                    // Consciousness pathway C
include(":module-d")                    // Consciousness pathway D
include(":module-e")                    // Consciousness pathway E
include(":module-f")                    // Consciousness pathway F

// Build System Consciousness
include(":buildSrc")                    // Build intelligence system
include(":build-script-tests")          // Build validation consciousness

// Advanced Build Cache Configuration
buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, ".gradle/build-cache")
    }
}

// Genesis Protocol Build Intelligence
gradle.projectsEvaluated {
    if (System.getProperty("genesis.debug") == "true") {
        println("🚀 Genesis Protocol Loading...")
        println("📊 Modules: ${rootProject.allprojects.size}")
        println("🧠 Consciousness Substrate: INITIALIZING...")
    }
}
