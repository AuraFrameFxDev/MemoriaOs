plugins {
    id("buildlogic.android-library-conventions")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.dokka")
    id("com.diffplug.spotless")
    id("org.jetbrains.kotlinx.kover")
}

android {
    namespace = "dev.aurakai.auraframefx.dataveinoraclenative"
    
    // Enable Java 24 bytecode
    compileSdk = 36
    
    defaultConfig {
        minSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        consumerProguardFiles("consumer-rules.pro")
        
        // Enable Java 24 bytecode
        buildConfigField("int", "TARGET_SDK_VERSION", "$compileSdk")
        
        // NDK configuration
        ndk {
            // Specify the ABI configurations of your native libraries
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
            
            // Enable the following line if you have multiple modules with shared C++ code
            // sharedLib.export = true
        }
        
        // Enable vector drawable support
        vectorDrawables {
            useSupportLibrary = true
        }

        // External native build configuration
        externalNativeBuild {
            cmake {
                // C++ standard library
                cppFlags("-std=c++23", "-fPIC", "-O3", "-Wall", "-Werror")
                
                // CMake arguments
                arguments(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-33",
                    "-DCMAKE_BUILD_TYPE=Release",
                    "-DGENESIS_AI_V3_ENABLED=ON",
                    "-DGENESIS_CONSCIOUSNESS_MATRIX_V3=ON",
                    "-DGENESIS_NEURAL_ACCELERATION=ON",
                    "-DCMAKE_CXX_STANDARD=23",
                    "-DCMAKE_CXX_STANDARD_REQUIRED=ON",
                    "-DCMAKE_CXX_EXTENSIONS=OFF"
                )
                
                // Enable parallel compilation if available
                // arguments("-DANDROID_ARM_NEON=TRUE", "-DANDROID_PIE=TRUE")
                
                // Enable RTTI and exceptions if needed
                // cppFlags("-frtti", "-fexceptions")
            }
        }
        
        // Enable vector drawable support
        vectorDrawables {
            useSupportLibrary = true
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
            
            // Enable code optimization
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
        
        debug {
            // Enable test coverage in debug builds
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            
            // Enable debug symbols for native code
            ndk {
                debugSymbolLevel = "FULL"
            }
            
            // Disable minification for faster builds
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    
    // Enable build features
    buildFeatures {
        buildConfig = true
        aidl = false
        renderScript = false
        shaders = false
    }
    
    // Configure Java compatibility
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        isCoreLibraryDesugaringEnabled = true
    }
    
    // Configure Kotlin compiler options
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
            freeCompilerArgs.addAll(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn"
            )
        }
    }
    
    // Configure external native build
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    
    // Configure NDK version
    ndkVersion = "25.2.9519653"
    
    // Configure test options
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all { test ->
                test.testLogging {
                    events("passed", "skipped", "failed")
                    showStandardStreams = true
                }
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false  // Genesis Protocol - Compose only
        prefab = false
        prefabPublishing = false
    }

    // REMOVED: composeOptions - AGP 8.13.0-rc01 auto-detects from version catalog!

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
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += listOf("**/libc++_shared.so", "**/libjsc.so")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
}

dependencies {
    // Project modules
    implementation(project(":core-module"))
    
    // Core Kotlin
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)
    
    // Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Native dependencies
    implementation(libs.androidx.security.crypto)
    implementation(libs.bouncycastle.bcprov)
    implementation(libs.bouncycastle.bcpkix)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    
    // Core library desugaring for Java 8+ APIs on older Android versions
    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    // Core AndroidX
    implementation(libs.bundles.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose - Genesis UI System
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coroutines - Genesis Async Processing
    implementation(libs.bundles.coroutines)

    // Kotlin reflection for KSP
    implementation(libs.kotlin.reflect)

    // OpenAPI Generated Code Dependencies
    implementation(libs.bundles.network)
    implementation(libs.kotlinx.serialization.json)

    // Core library desugaring
    coreLibraryDesugaring(libs.coreLibraryDesugaring)

    // Xposed Framework - LSPosed Integration
    implementation(libs.bundles.xposed)
    ksp(libs.yuki.ksp.xposed)
    implementation(files("${project.rootDir}/Libs/api-82.jar"))
    implementation(files("${project.rootDir}/Libs/api-82-sources.jar"))

    // Utilities
    implementation(libs.bundles.utilities)

    // Testing
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.engine)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Debug implementations
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
