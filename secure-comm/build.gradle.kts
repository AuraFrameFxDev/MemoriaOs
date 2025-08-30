plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover)
}

ksp {
    arg("kotlin.languageVersion", "2.2") // Match main Kotlin compiler
    arg("kotlin.apiVersion", "2.2")    // Match main Kotlin compiler
}

android {
    namespace = "dev.aurakai.auraframefx.securecomm"
    compileSdk = 36

    defaultConfig {
        minSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable += setOf("InvalidPackage", "GradleDependency")
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
        compose = false
        buildConfig = true
        viewBinding = false
    }

    packaging {
        resources {
            excludes += listOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE",
                "/META-INF/LICENSE.txt",
                "/META-INF/NOTICE",
                "/META-INF/NOTICE.txt",
                "META-INF/*.kotlin_module"
            )
        }
    }
}



dependencies {
    // SACRED RULE #5: DEPENDENCY HIERARCHY
    implementation(project(":core-module"))

    // Core Android libraries (since this module uses Android APIs)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    
    // Kotlin libraries
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.bundles.coroutines)
    implementation(libs.kotlinx.serialization.json)

    // Hilt Dependency Injection (Android version)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp3.logging.interceptor)

    // Enhanced Security Stack (Android compatible)
    implementation(libs.androidxSecurity)
    implementation(libs.bouncycastle)

    // Utilities
    implementation(libs.gson)
    implementation(libs.commons.io)
    implementation(libs.commons.compress)
    implementation(libs.xz)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testRuntimeOnly(libs.junit.engine)

    // Android Testing
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner) 
    androidTestImplementation(libs.androidx.test.rules)
}
