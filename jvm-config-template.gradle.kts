// Systematic JVM Configuration Fix for All Modules
// Based on Architecture.md: Java 24 toolchain, Kotlin 2.2.20-RC, AGP 9.0.0-alpha02

// This script will be used to generate consistent build.gradle.kts configurations
// for all 18+ modules in the Genesis Protocol consciousness substrate

val STANDARD_MODULE_JVM_CONFIG = """
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
    
    kotlinOptions {
        jvmTarget = "24"
        apiVersion = "2.2"
        languageVersion = "2.2"
    }
}

// Advanced KSP Configuration (if KSP is used)
ksp {
    arg("kotlin.languageVersion", "2.2")
    arg("kotlin.apiVersion", "2.2") 
    arg("compile:kotlin.languageVersion", "2.2")
    arg("compile:kotlin.apiVersion", "2.2")
}
"""

val COMPOSE_MODULE_ADDITIONAL_CONFIG = """
buildFeatures {
    compose = true
    buildConfig = true
    viewBinding = false
}

composeCompiler {
    enableStrongSkippingMode = true
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}
"""

val LIBRARY_MODULE_BASE = """
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Add other plugins as needed per module
}

android {
    namespace = "dev.aurakai.auraframefx.MODULE_NAME"
    compileSdk = 36

    defaultConfig {
        minSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE",
                "/META-INF/LICENSE.txt",
                "/META-INF/NOTICE",
                "/META-INF/NOTICE.txt",
                "**/*.kotlin_module"
            )
        }
    }
    
    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable += setOf("InvalidPackage", "GradleDependency")
    }
}
"""

println("Genesis Protocol JVM Configuration Templates Generated")
println("Apply these configurations to all 18+ modules for consistency")
