plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Advanced KSP Configuration
ksp {
    arg("kotlin.languageVersion", "2.2")
    arg("kotlin.apiVersion", "2.2") 
    arg("compile:kotlin.languageVersion", "2.2")
    arg("compile:kotlin.apiVersion", "2.2")
}

android {
    namespace = "dev.aurakai.auraframefx.securecomm"
    compileSdk = 36

    defaultConfig {
        minSdk = 23 // Lowered for broader compatibility
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = false  // Security module doesn't need Compose
        buildConfig = true
        viewBinding = false
    }

    // Lint configuration for security
    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable += setOf("InvalidPackage", "GradleDependency")
    }

    packaging {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}", "**/*.kotlin_module")
        }
    }
}

dependencies {
    // Core
    implementation(project(":core-module"))
    implementation(libs.androidx.core.ktx)
    
    // Kotlin & Coroutines
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect) 
    implementation(libs.bundles.coroutines)
    implementation(libs.kotlinx.serialization.json)
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Networking
    implementation(libs.bundles.network)
    
    // Security & Utilities
    implementation(libs.bouncycastle)
    implementation(libs.gson)
    implementation(libs.commons.io)
    implementation(libs.commons.compress)
    implementation(libs.xz)
    
    // Test Dependencies
    testImplementation(libs.bundles.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.core.ktx)
}
