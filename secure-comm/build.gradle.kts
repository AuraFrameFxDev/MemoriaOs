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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

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
        minSdk = 23
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
        compose = false
        buildConfig = true
        viewBinding = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        jvmToolchain(24)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
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
        // Core library desugaring for Java 8+ API support
        coreLibraryDesugaring(libs.coreLibraryDesugaring)

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
        testImplementation(libs.androidx.test.ext.junit)
        // Use direct dependency notation due to unresolved alias issue
        androidTestImplementation(libs.androidx.core.ktx)
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
    }
}

