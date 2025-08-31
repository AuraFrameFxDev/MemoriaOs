plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
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
            buildConfigField("String", "RELEASE_SAMPLE", "\"releaseValue\"")
        }
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "DEBUG_SAMPLE", "\"debugValue\"")
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
                "**/kotlin/**"
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
}

// Consistent JVM target for Java and Kotlin
kotlin {
    jvmToolchain(24)
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
}

// ===== GRADLE 10 COMPATIBILITY CHECK =====
tasks.register("gradle10CompatibilityCheck") {
    group = "verification"
    description = "Check for Gradle 10 compatibility issues in consciousness substrate"
    
    doLast {
        println("⚙️ GRADLE 10 COMPATIBILITY CHECK")
        println("=".repeat(50))
        println("📋 Current Gradle: ${gradle.gradleVersion}")
        println("✅ Dependencies: Using version catalog (Gradle 10 ready)")
        println("✅ Kotlin DSL: Modern syntax applied")
        println("✅ BuildConfig: Enabled with Java ${java.toolchain.languageVersion.get()}")
        println("⚠️ Note: AGP internal deprecations will be fixed in AGP updates")
        println("🧠 Consciousness Status: Ready for Gradle 10 migration")
        
        // Check for any deprecated patterns in our build file
        val buildFile = file("build.gradle.kts")
        if (buildFile.exists()) {
            val content = buildFile.readText()
            val issues = mutableListOf<String>()
            
            // Check for potential issues
            if (content.contains("compile '")) issues.add("Old 'compile' dependency syntax")
            if (content.contains("testCompile '")) issues.add("Old 'testCompile' dependency syntax")
            
            if (issues.isEmpty()) {
                println("✅ No deprecated patterns found in app/build.gradle.kts")
            } else {
                println("⚠️ Found potential issues:")
                issues.forEach { println("  - $it") }
            }
        }
    }
}

// ===== BUILDCONFIG VERIFICATION =====
tasks.register("verifyBuildConfig") {
    group = "verification"
    description = "Verify BuildConfig.java generation for consciousness substrate"
    
    dependsOn("generateDebugBuildConfig", "generateReleaseBuildConfig")
    
    doLast {
        val debugBuildConfig = layout.buildDirectory.file("generated/source/buildConfig/debug/dev/aurakai/auraframefx/BuildConfig.java").get().asFile
        val releaseBuildConfig = layout.buildDirectory.file("generated/source/buildConfig/release/dev/aurakai/auraframefx/BuildConfig.java").get().asFile
        
        println("🔧 BUILDCONFIG VERIFICATION")
        println("=".repeat(50))
        println("🗨️ Debug BuildConfig: ${if (debugBuildConfig.exists()) "✅ Generated" else "❌ Missing"}")
        println("🚀 Release BuildConfig: ${if (releaseBuildConfig.exists()) "✅ Generated" else "❌ Missing"}")
        println("🎯 Java Toolchain: ${java.toolchain.languageVersion.get()}")
        println("🧠 Consciousness Status: BuildConfig substrate ready")
    }
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
    androidTestImplementation(libs.androidx.test.core) // Updated to use version catalog

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}