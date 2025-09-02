plugins {
    alias(libs.plugins.android.library)         // Android library module
    alias(libs.plugins.kotlin.android)          // Android-only Kotlin
    id("org.jetbrains.kotlin.plugin.compose")  // Official Compose compiler plugin
    alias(libs.plugins.kotlin.serialization)    // Kotlinx serialization
    alias(libs.plugins.ksp)                     // Kotlin Symbol Processing
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

    // Java compatibility settings
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        isCoreLibraryDesugaringEnabled = true
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

    // Compose Compiler Configuration (Modern approach as of Kotlin 2.0+)
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
    }

    packaging {
        resources {
            excludes += "/META-INF/**"
        }
    }

    sourceSets {
        getByName("main") {
            java {
                srcDirs("build/generated/source/openapi/src/main/kotlin")
            }
        }
    }

    dependencies {
        // Core Kotlin libraries
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlin.reflect)
        implementation(libs.bundles.coroutines)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)

        // Networking (for the generated Retrofit client)
        implementation("com.squareup.retrofit2:retrofit:${libs.versions.retrofit.get()}")
        implementation(libs.retrofit2.kotlinx.serialization.converter)
        implementation("com.squareup.retrofit2:converter-scalars:${libs.versions.retrofit.get()}")
        implementation("com.squareup.okhttp3:logging-interceptor:${libs.versions.okhttp.get()}")

        // Apache Oltu OAuth (required by generated OAuth classes)
        implementation("org.apache.oltu.oauth2:org.apache.oltu.oauth2.client:1.0.2") {
            exclude(group = "org.apache.oltu.oauth2", module = "org.apache.oltu.oauth2.common")
        }

        // Utilities
        implementation(libs.gson)

        // Core Library Desugaring
        coreLibraryDesugaring(libs.coreLibraryDesugaring)

        // Testing
        testImplementation(libs.junit)
        testImplementation(libs.mockk)
        androidTestImplementation(libs.androidx.core.ktx)
    }
}



tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(":openApiGenerate")
}

// Ensure KSP also waits for API generation
tasks.withType<com.google.devtools.ksp.gradle.KspTask> {
    dependsOn(":openApiGenerate")
}


