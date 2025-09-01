/*
 * LSPosed Module Configuration
 * This module provides Xposed framework integration using Yuki API
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.lsposed.lsparanoid")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

// Configure Java 17 toolchain for this module (LTS for better compatibility)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

android {
    namespace = "com.aura.genesis.lsposed"
    
    defaultConfig {
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Enable multidex support
        multiDexEnabled = true
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
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        
        // Enable Java 8+ API desugaring support
        isCoreLibraryDesugaringEnabled = true
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xjvm-default=all",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-receivers"
        )
    }
    
    buildFeatures {
        buildConfig = true
        viewBinding = true
        aidl = true
    }
    
    // Enable view binding
    buildFeatures.viewBinding = true
    
    // Enable data binding
    buildFeatures.dataBinding = true
    
    // Enable Compose if needed
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    
    // Enable Java 8+ API desugaring
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    
    // Lifecycle components
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    
    // YukiHookAPI - Core dependencies
    implementation(libs.yuki) {
        // Exclude any transitive dependencies that might cause conflicts
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    }
    
    // YukiHookAPI - KSP processor for Xposed
    ksp(libs.yuki.ksp.xposed)
    
    // LSPosed API (compile-only, provided by the runtime)
    compileOnly(libs.xposed.api)
    
    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    
    // Hilt for dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Security
    implementation(libs.androidxSecurity)
    
    // Multidex support
    implementation("androidx.multidex:multidex:2.0.1")
    
    // Java 8+ API desugaring
    coreLibraryDesugaring(libs.coreLibraryDesugaring)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    
    // Debug dependencies
    debugImplementation(libs.leakcanary.android)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // Optional: Add LSParanoid annotations for additional obfuscation control
    implementation("org.lsposed.lsparanoid:lsparanoid-annotations:0.6.0")
}

// LSParanoid configuration - String obfuscation for security
lsparanoid {
    seed = 0x2A // Consistent seed for reproducible builds
    includeAsSharedUuid = true // Share UUID across build variants
    variantFilter = "" // Apply to all variants
    // Enable additional obfuscation features
    enableObfuscation = true
    enableStringObfuscation = true
    enableMethodObfuscation = true
    
    // Configure which packages to include/exclude
    includes = [
        "com.aura.genesis.lsposed"
    ]
    
    // Exclude certain classes or packages if needed
    excludes = [
        "com.aura.genesis.lspoed.BuildConfig"
    ]
}
