plugins {
    id("buildlogic.android-library-conventions")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.dokka")
    id("com.diffplug.spotless")
    id("org.jetbrains.kotlinx.kover")
}

// Configure KSP
ksp {
    // Set Kotlin language and API version
    arg("kotlin.languageVersion", "2.2")
    arg("kotlin.apiVersion", "2.2")
    
    // Enable incremental processing for better build performance
    arg("room.incremental", "true")
    
    // Enable KSP's experimental parallel processing
    arg("room.schemaLocation", "$projectDir/schemas")
    
    // Enable K2 compiler
    arg("ksp.incremental.k2", "true")
    
    // Enable KSP's experimental mode for better performance
    arg("ksp.incremental.log", "true")
}

android {
    namespace = "dev.aurakai.auraframefx.securecomm"
    
    // Enable Java 24 bytecode
    compileSdk = 36
    
    defaultConfig {
        minSdk = 23  // Required for Android KeyStore APIs
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        
        consumerProguardFiles("consumer-rules.pro")
        
        // Enable vector drawable support
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // Enable Java 24 bytecode
        buildConfigField("int", "TARGET_SDK_VERSION", "$compileSdk")
    }

    lint {
        // Configure lint options
        abortOnError = false
        checkReleaseBuilds = false
        disable += setOf("InvalidPackage", "GradleDependency")
        
        // Enable lint checks for test sources
        checkTestSources = true
        ignoreTestSources = false
        
        // Enable all warnings as errors
        warningsAsErrors = false
        
        // Enable HTML report
        htmlReport = true
        
        // Configure lint baseline
        baseline = file("lint-baseline.xml")
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
            // Enable test coverage in debug builds
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            
            // Enable code shrinking in debug for faster builds
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
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Security
    implementation(libs.androidx.security.crypto)
    implementation(libs.bouncycastle.bcprov)
    implementation(libs.bouncycastle.bcpkix)
    
    // Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)  // For testing Kotlin Flows
    
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    
    // Core library desugaring for Java 8+ APIs on older Android versions
    coreLibraryDesugaring(libs.android.desugarJdkLibs)
    
    // Logging
    implementation(libs.timber)
    
    // For testing with AndroidX Test and Hilt
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestUtil(libs.androidx.test.orchestrator)

    // Utilities
    implementation(libs.gson)
    implementation(libs.commons.io)
    implementation(libs.commons.compress)

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
