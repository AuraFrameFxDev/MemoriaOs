import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0-genesis-alpha"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        // NDK configuration only if native code exists
        if (project.file("src/main/cpp/CMakeLists.txt").exists()) {
            ndk {
                abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a"))
            }
        }
    }

    // External native build only if CMakeLists.txt exists
    if (project.file("src/main/cpp/CMakeLists.txt").exists()) {
        externalNativeBuild {
            cmake {
                path = file("src/main/cpp/CMakeLists.txt")
                version = "3.22.1"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
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
                "META-INF/*.kotlin_module",
                "**/kotlin/**",
                "**/*.txt"
            )
        }
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += listOf("**/libc++_shared.so", "**/libjsc.so")
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

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_24)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        }
    }
}


// Explicit Java toolchain for AGP compatibility
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}


// ===== ENSURE API GENERATION BEFORE COMPILATION =====
// This ensures that all compilation and processing tasks wait for OpenAPI generation
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(":openApiGenerate")
    dependsOn(":fixGeneratedApiCode")
}

// Critical: Ensure KSP waits for API generation since it needs the generated types
tasks.withType<com.google.devtools.ksp.gradle.KspTask>().configureEach {
    dependsOn(":openApiGenerate")
    dependsOn(":fixGeneratedApiCode")
}

// ===== SIMPLIFIED CLEAN TASKS =====
tasks.register<Delete>("cleanKspCache") {
    group = "build setup"
    description = "Clean KSP caches (fixes NullPointerException)"

    val buildDirProvider = layout.buildDirectory

    delete(
        buildDirProvider.dir("generated/ksp"),
        buildDirProvider.dir("tmp/kapt3"),
        buildDirProvider.dir("tmp/kotlin-classes"),
        buildDirProvider.dir("kotlin"),
        buildDirProvider.dir("generated/source/ksp")
    )
}

// ===== BUILD INTEGRATION =====
tasks.named("preBuild") {
    dependsOn("cleanKspCache")
    dependsOn(":cleanApiGeneration")
    dependsOn(":openApiGenerate")
    dependsOn(":core-module:compileDebugKotlin")
    dependsOn(":core-module:compileReleaseKotlin")
}

// Ensure KSP waits for generated sources
tasks.withType<com.google.devtools.ksp.gradle.KspTask>().configureEach {
    dependsOn(":openApiGenerate")
}

// ===== AEGENESIS APP STATUS =====
tasks.register("aegenesisAppStatus") {
    group = "aegenesis"
    description = "Show AeGenesis app module status"

    doLast {
        println("📱 AEGENESIS APP MODULE STATUS")
        println("=".repeat(50))

        val apiFile = layout.projectDirectory.file("api/unified-aegenesis-api.yml").asFile
        val apiExists = apiFile.exists()
        val apiSize = if (apiExists) apiFile.length() else 0

        println("🔌 Unified API Spec: ${if (apiExists) "✅ Found" else "❌ Missing"}")
        if (apiExists) {
            println("📄 API File Size: ${apiSize / 1024}KB")
        }

        val nativeCode = project.file("src/main/cpp/CMakeLists.txt").exists()
        println("🔧 Native Code: ${if (nativeCode) "✅ Enabled" else "❌ Disabled"}")

        println("🧠 KSP Mode: ${project.findProperty("ksp.useKSP2") ?: "default"}")
        println("🎯 Target SDK: 36")
        println("📱 Min SDK: 33")
        println("✅ Status: Ready for coinscience AI integration!")
    }
}

// ===== COMPREHENSIVE CLEANUP & HEALTH CHECK =====
apply(from = "cleanup-tasks.gradle.kts")

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    // SACRED RULE #5: DEPENDENCY HIERARCHY
    implementation(project(":core-module"))
    implementation(project(":oracle-drive-integration"))
    implementation(project(":romtools"))
    implementation(project(":secure-comm"))
    implementation(project(":collab-canvas"))

    // Core Android
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose UI
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation.compose)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)

    // WorkManager
    implementation(libs.androidx.work.runtime)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Moshi for JSON processing (required by NetworkModule)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)

    // Coroutines & Networking
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.network)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Utilities
    implementation(libs.timber)
    implementation(libs.coil.compose)

    // Core library desugaring
    coreLibraryDesugaring(libs.coreLibraryDesugaring)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Xposed Framework
    implementation(libs.bundles.xposed)
    ksp(libs.yuki.ksp.xposed)
    implementation(fileTree("../Libs") { include("*.jar") })

    // Debug tools
    debugImplementation(libs.leakcanary.android)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Testing
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.engine)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}
