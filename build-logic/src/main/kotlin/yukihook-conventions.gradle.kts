plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.lsposed.lsparanoid")
}

android {
    // Standard Android configuration
    compileSdk = 36
    
    defaultConfig {
        minSdk = 33
        targetSdk = 36
        
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
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
    
    kotlinOptions {
        jvmTarget = "24"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xjvm-default=all",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}

dependencies {
    // Xposed Framework - YukiHookAPI (Standardized)
    implementation(libs.bundles.xposed)
    
    // Legacy Xposed API (compatibility)
    implementation(files("${project.rootDir}/Libs/api-82.jar"))
    implementation(files("${project.rootDir}/Libs/api-82-sources.jar"))
    
    // Core Android dependencies
    implementation(libs.bundles.androidx.core)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

// LSParanoid configuration
lsparanoid {
    seed = 0x2A // Consistent seed across all modules
    includeAsSharedUuid = true
}

// KSP configuration
ksp {
    // Add any KSP arguments here if needed
    arg("YUKIHOOK_PACKAGE_NAME", project.group.toString())
}
