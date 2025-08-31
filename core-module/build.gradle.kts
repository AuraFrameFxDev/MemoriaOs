plugins {
    id("com.android.library")
    //id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    // If you use Dokka, Spotless, Kover in this module, add their IDs here too:
    // id("org.jetbrains.dokka")
    // id("com.diffplug.spotless")
    // id("org.jetbrains.kotlinx.kover")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

android {
    namespace = "dev.aurakai.auraframefx.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 33
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
        viewBinding = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }



    packaging {
        resources {
        }

        sourceSets {
            getByName("main") {
                java.srcDirs(rootProject.layout.buildDirectory.dir("core-module/generated/source/openapi/src/main/kotlin"))
            }
        }
    }

    dependencies {
        // Core Kotlin libraries
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlin.reflect)
        implementation(libs.bundles.coroutines)
        implementation(libs.kotlinx.serialization.json)

        // Networking (for the generated Retrofit client)
        implementation(libs.retrofit)
        implementation(libs.retrofit.converter.kotlinx.serialization)
        implementation(libs.okhttp3.logging.interceptor)

        // Utilities
        implementation(libs.gson)

        // Security

        // Testing
        testImplementation(libs.junit)
        testImplementation(libs.mockk)

        androidTestImplementation(libs.androidx.core.ktx)
    }

// The duplicate java { toolchain { ... } } block that was here has been removed.

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        dependsOn(":openApiGenerate")
    }
}


