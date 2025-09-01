/*
 * Android Application conventions for AeGenesis project
 * Applies common conventions and configures Android application specific settings
 */

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("buildlogic.kotlin-common-conventions")
}

android {
    namespace = "com.aegenesis.${project.name.replace("-", "")}"
    
    compileSdk = 35
    
    defaultConfig {
        applicationId = "com.aegenesis.${project.name.replace("-", "").toLowerCase()}"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
    
    kotlinOptions {
        jvmTarget = "24"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/*.md"
            excludes += "META-INF/CHANGES"
            excludes += "META-INF/README.md"
        }
    }
}

// Configure test tasks
tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Configure build types for different environments
android {
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            
            // Enable debugging for dev builds
            isDebuggable = true
            isDefault = true
        }
        
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            
            // Enable debugging for staging builds
            isDebuggable = true
        }
        
        create("production") {
            dimension = "environment"
            // No suffix for production
        }
    }
}

// Configure APK/AAB naming
android.applicationVariants.all {
    val variant = this
    variant.outputs
        .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
        .forEach { output ->
            val outputFileName = "${project.name}-${variant.baseName}-${variant.versionName}.${variant.versionCode}.${if (variant.buildType.isMinifyEnabled) "release" else "debug"}${output.outputFile.extension.replace(".", "")}"
            output.outputFileName = outputFileName
        }
}

// Configure dependency updates
dependencyUpdates {
    checkForGradleUpdate = true
    outputDir = "build/dependencyUpdates"
    outputFormatter = "json"
    revision = "release"
    checkConstraints = true
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
