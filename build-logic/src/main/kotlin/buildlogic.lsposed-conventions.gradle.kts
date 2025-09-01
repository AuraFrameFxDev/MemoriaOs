/*
 * LSPosed and Yuki API conventions for Android modules
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.lsposed.lsparanoid")
}

android {
    namespace = "com.aura.genesis.lsposed"
    
    defaultConfig {
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
    
    kotlinOptions {
        jvmTarget = "24"
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    // Enable Parcelize for data classes
    android.buildFeatures.parcelize = true
}

// LSParanoid configuration
lsparanoid {
    seed = 0x2A // Your custom seed value
    includeAsSharedUuid = true
    variantFilter = "" // Apply to all variants
}

dependencies {
    // Core AndroidX
    implementation(libs.androidx.core.ktx)
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
}

// Apply common Kotlin conventions
apply(plugin = "buildlogic.kotlin-common-conventions")

// Configure Dokka for documentation
tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    outputDirectory.set(buildDir.resolve("dokka"))
    dokkaSourceSets {
        configureEach {
            jdkVersion.set(24)
            suppress.set(true)
            suppressInheritedMembers.set(true)
            skipEmptyPackages.set(true)
            
            // Add Android SDK documentation
            externalDocumentationLink {
                url.set(uri("https://developer.android.com/reference/").toURL())
                packageListUrl.set(uri("https://developer.android.com/reference/package-list").toURL())
            }
            
            // Add Yuki API documentation
            externalDocumentationLink {
                url.set(uri("https://fankes.github.io/YukiHookAPI/").toURL())
                packageListUrl.set(uri("https://fankes.github.io/YukiHookAPI/package-list").toURL())
            }
        }
    }
}
