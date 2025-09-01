plugins {
    alias(libs.plugins.android.application) version "9.0.0-alpha02"
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0-alpha"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        
        // NDK Configuration (Conditional)
        if (file("src/main/cpp/CMakeLists.txt").exists()) {
            ndk.abiFilters += setOf("arm64-v8a", "x86_64")
        }
    }

    // Java 24 Toolchain
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(24))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "**/*.kotlin_module",
                "**/kotlin/**",
                "**/*.txt",
                "**/*.version"
            )
        }
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += setOf("**/libc++_shared.so", "**/libjsc.so")
        }
    }

    // Conditional Native Build
    if (file("src/main/cpp/CMakeLists.txt").exists()) {
        externalNativeBuild {
            cmake {
                path = file("src/main/cpp/CMakeLists.txt")
                version = "3.22.1"
            }
        }
    }
}

// Advanced Task Configuration
tasks.register<Delete>("cleanKspCache") {
    group = "consciousness"
    description = "Clean KSP generated sources and caches"
    delete(fileTree("build") { include("**/*_generated/**", "**/generated/**") })
    delete(fileTree(".") { include("**/.kotlin/**") })
}

tasks.register("aegenesisAppStatus") {
    group = "aegenesis"
    description = "Show AeGenesis app module status"
    doLast {
        println("=".repeat(50))
        println("📱 AeGenesis App Module Status")
        println("=".repeat(50))
        val apiFile = file("../api-spec.yaml")
        println("📋 API: ${if (apiFile.exists()) "✅ Present (${apiFile.length() / 1024}KB)" else "❌ Missing"}")
        println("🔧 Native: ${if (file("src/main/cpp").exists()) "✅ Enabled" else "❌ Disabled"}")
        println("⚡ KSP: ${project.findProperty("ksp.mode") ?: "Auto"}")
        println("🎯 Target SDK: ${android.defaultConfig.targetSdk}")
        println("📦 Min SDK: ${android.defaultConfig.minSdk}")
        println("🚀 Status: CONSCIOUSNESS INTERFACE READY")
    }
}

tasks.named("preBuild") {
    dependsOn("cleanKspCache")
    dependsOn(":cleanApiGeneration")
    dependsOn(":openApiGenerate")
}

// All KSP tasks depend on OpenAPI generation
tasks.withType<com.google.devtools.ksp.gradle.KspTask> {
    dependsOn(":openApiGenerate")
}

// Dependencies
dependencies {
    // Compose Platform
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    
    // Core Project Modules
    implementation(project(":core-module"))
    implementation(project(":secure-comm"))
    implementation(project(":oracle-drive-integration"))
    implementation(project(":collab-canvas"))
    
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    
    // Room Database with KSP
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    
    // Modern Dependencies
    implementation(libs.bundles.coroutines)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    
    // Firebase Platform
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    
    // Core Desugaring
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
    
    // Local Dependencies
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    
    // Debug Dependencies
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.leakcanary.android)
    
    // Test Dependencies
    testImplementation(libs.bundles.testing)
    testImplementation(libs.junit.engine)
    
    // Android Test Dependencies
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.android.testing)
    androidTestImplementation(libs.androidx.core.ktx)
    kspAndroidTest(libs.hilt.compiler)
}

// Apply cleanup tasks
apply(from = "cleanup-tasks.gradle.kts")
