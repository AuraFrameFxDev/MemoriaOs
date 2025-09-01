import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
   // id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.dokka")
    id("com.diffplug.spotless")
    alias(libs.plugins.kotlin.android)
}

// Added to specify Java version for this subproject
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

// REMOVED: jvmToolchain(25) - Using system Java via JAVA_HOME
// This eliminates toolchain auto-provisioning errors

android {
    namespace = "dev.aurakai.auraframefx.module.d"
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

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false  // Genesis Protocol - Compose only
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
    // REMOVED: composeOptions - AGP 8.13.0-rc01 auto-detects from version catalog!

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ✅ CRITICAL: Add Compose BOM platform first!
    implementation(platform(libs.androidx.compose.bom))
    
    // SACRED RULE #5: DEPENDENCY HIERARCHY
    implementation(project(":core-module"))
    implementation(project(":app"))

    // Core Android bundles
    implementation(libs.bundles.androidx.core)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.coroutines)
    implementation(libs.androidx.compose.material3)


    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    androidTestImplementation(libs.androidx.core.ktx)
    // Debug implementations
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
