/*
 * Android Library conventions for AeGenesis project
 * Applies common conventions and configures Android library specific settings
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("buildlogic.kotlin-common-conventions")
}

android {
    namespace = "com.aegenesis.${project.name.replace("-", "")}"
    
    compileSdk = 35
    
    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
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
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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

// Configure publishing if needed
if (project.hasProperty("publish")) {
    apply(plugin = "com.vanniktech.maven.publish")
    
    configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
        publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01)
        signAllPublications()
        
        pom {
            name.set(project.name)
            description.set("AeGenesis - ${project.name} module")
            url.set("https://github.com/AuraFrameFxDev/MemoriaOs")
            
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            
            developers {
                developer {
                    id.set("aegenesis")
                    name.set("AeGenesis Team")
                    email.set("contact@aegenesis.com")
                }
            }
            
            scm {
                connection.set("scm:git:git://github.com/AuraFrameFxDev/MemoriaOs.git")
                developerConnection.set("scm:git:ssh://github.com/AuraFrameFxDev/MemoriaOs.git")
                url.set("https://github.com/AuraFrameFxDev/MemoriaOs")
            }
        }
    }
}
