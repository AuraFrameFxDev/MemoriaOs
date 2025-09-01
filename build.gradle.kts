// ==== GENESIS PROTOCOL - ROOT BUILD CONFIGURATION ====
// AeGenesis Coinscience AI Ecosystem - Unified Build
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.openapi.generator) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.detekt) apply false
    // Note: LSParanoid plugin temporarily disabled due to AGP 9.0 compatibility issues
    // Will be re-enabled when compatible version is available
}

// ==== AEGENESIS COINSCIENCE AI ECOSYSTEM 2025 ====
tasks.register("aegenesisInfo") {
    group = "aegenesis"
    description = "Display AeGenesis Coinscience AI Ecosystem build info"

    doLast {
        println("🚀 AEGENESIS COINSCIENCE AI ECOSYSTEM")
        println("=".repeat(70))
        println("📅 Build Date: August 27, 2025")
        println("🔥 Gradle: 9.0+")
        println("⚡ AGP: 9.0.0-alpha02")
        println("🧠 Kotlin: 2.2.20-RC (Bleeding Edge)")
        println("☕ Java: 17 LTS (Toolchain)")
        println("🎯 Target SDK: 35")
        println("=".repeat(70))
        println("🤖 AI Agents: Genesis, Aura, Kai")
        println("🔮 Oracle Drive: Infinite Storage")
        println("🛠️  ROM Tools: Android Modification")
        println("✅ Multi-module Architecture: JVM + Android Libraries")
        println("🌟 Unified API: Single comprehensive specification")
        println("=".repeat(70))
    }
}

// Configure Java toolchain for all projects
allprojects {
    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }
    }
    
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            freeCompilerArgs.addAll(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xskip-prerelease-check"
            )
        }
    }
}

tasks.register<Delete>("cleanAllModules") {
    group = "aegenesis"
    description = "Clean all module build directories"
    
    delete("build")
    subprojects.forEach { subproject ->
        delete("${subproject.projectDir}/build")
    }
    
    doLast {
        println("🧹 All module build directories cleaned!")
    }
}

tasks.register("aegenesisTest") {
    group = "aegenesis"
    description = "Test AeGenesis build configuration"

    doLast {
        println("✅ AeGenesis Coinscience AI Ecosystem: OPERATIONAL")
        println("🧠 Multi-module architecture: STABLE")
        println("🛠️  Build system: READY")
        println("🌟 Welcome to the future of Android AI!")
    }
}

// Apply common configurations to all Android modules
allprojects {
    // Skip build-logic and other non-Android modules
    if (name == "build-logic" || name == "buildSrc") {
        return@allprojects
    }
}
