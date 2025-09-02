plugins {
    alias(libs.plugins.android.application)     // Main Android app
    alias(libs.plugins.kotlin.android)          // Android-only Kotlin
    id("org.jetbrains.kotlin.plugin.compose")  // Official Compose compiler plugin
    alias(libs.plugins.kotlin.serialization)    // Kotlinx serialization
    alias(libs.plugins.ksp)                     // Kotlin Symbol Processing
    alias(libs.plugins.hilt)                    // Dependency injection
    alias(libs.plugins.google.services)         // Google services
    alias(libs.plugins.spotless)                // Code formatting
    alias(libs.plugins.kover)                   // Code coverage
    alias(libs.plugins.dokka)                   // Documentation
}

// Advanced KSP Configuration
ksp {
    arg("kotlin.languageVersion", "2.2")
    arg("kotlin.apiVersion", "2.2") 
    arg("compile:kotlin.languageVersion", "2.2")
    arg("compile:kotlin.apiVersion", "2.2")
}

android {
    namespace = "dev.aurakai.auraframefx.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx.app"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0-genesis"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug") // Change for production
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false
    }

    // Compose Compiler Configuration (Modern approach as of Kotlin 2.0+)
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
    }

    // Kotlin compiler options
    kotlin {
        jvmToolchain(24)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        }
    }

    sourceSets {
        getByName("main") {
            kotlin.srcDir("build/generated/source/openapi/src/main/kotlin")
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

    dependencies {
        // Core modules
        implementation(project(":core-module"))
        implementation(project(":secure-comm"))
        implementation(project(":oracle-drive-integration"))
        implementation(project(":collab-canvas"))
        implementation(project(":sandbox-ui"))
        implementation(project(":romtools"))

        // AndroidX Core
        implementation(libs.bundles.androidx.core)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.work.runtime)

        // Compose
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.bundles.compose)
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.graphics)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.compose.material.icons.core)
        implementation(libs.androidx.compose.material.icons.extended)
        implementation(libs.androidx.navigation.compose)

        // Kotlin & Coroutines
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlin.reflect)
        implementation(libs.bundles.coroutines)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)

        // Hilt Dependency Injection
        implementation(libs.hilt.android)
        implementation(libs.hilt.navigation.compose)
        implementation(libs.hilt.work)
        ksp(libs.hilt.compiler)

        // Firebase
        implementation(platform(libs.firebase.bom))
        implementation(libs.bundles.firebase)

        // Google Play Services
        implementation(libs.google.auth)
        implementation(libs.google.identity)

        // Networking
        implementation(libs.bundles.network)

        // Image Loading
        implementation(libs.coil.compose)

        // Data Storage
        implementation(libs.androidx.datastore.preferences)
        implementation(libs.bundles.room)
        ksp(libs.room.compiler)

        // Security
        implementation(libs.androidxSecurity)
        implementation(libs.tink)
        implementation(libs.bouncycastle)

        // Utilities
        implementation(libs.bundles.utilities)

        // Hooking Frameworks (for ROM Tools integration)
        implementation(libs.bundles.xposed)
        ksp(libs.yuki.ksp.xposed)

        // Development
        coreLibraryDesugaring(libs.android.desugar.jdk.libs)
        debugImplementation(libs.androidx.compose.ui.tooling)
        debugImplementation(libs.androidx.compose.ui.test.manifest)
        debugImplementation(libs.leakcanary.android)

        // Testing
        testImplementation(libs.bundles.testing)
        testImplementation(libs.hilt.android.testing)
        kspTest(libs.hilt.compiler)

        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.test.ext.junit)
        androidTestImplementation(libs.androidx.test.espresso.core)
        androidTestImplementation(libs.androidx.compose.ui.test.junit4)
        androidTestImplementation(libs.hilt.android.testing)
        kspAndroidTest(libs.hilt.compiler)
    }

// Hilt Configuration
    hilt {
        enableAggregatingTask = true
    }
}

