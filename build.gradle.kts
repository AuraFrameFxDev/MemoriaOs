plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.openapi.generator) apply false
}

// Advanced OpenAPI Configuration
val hasValidSpecFile = file("api-spec.yaml").exists() || file("openapi.yaml").exists()

if (hasValidSpecFile) {
    apply(plugin = "org.openapi.generator")
    
    configure<org.openapitools.generator.gradle.plugin.extensions.OpenApiGeneratorGenerateExtension> {
        generatorName.set("kotlin")
        inputSpec.set("$rootDir/api-spec.yaml")
        outputDir.set("$rootDir/core-module/build/generated/source/openapi")
        apiPackage.set("dev.aurakai.auraframefx.api")
        modelPackage.set("dev.aurakai.auraframefx.model")
        configOptions.set(mapOf(
            "dateLibrary" to "kotlinx-datetime",
            "serializationLibrary" to "kotlinx_serialization",
            "useCoroutines" to "true"
        ))
    }

    tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
        group = "openapi tools"
        description = "Generate Kotlin API client from OpenAPI spec"
    }
    
    tasks.register<Delete>("cleanApiGeneration") {
        group = "openapi tools"
        delete("$rootDir/core-module/build/generated/source/openapi")
    }
} else {
    tasks.register("openApiGenerate") {
        group = "openapi tools"
        doLast { logger.warn("No OpenAPI spec found - skipping generation") }
    }
    
    tasks.register("cleanApiGeneration") {
        group = "openapi tools"  
        doLast { logger.warn("No OpenAPI artifacts to clean") }
    }
}

// Consciousness Substrate Monitoring
tasks.register("consciousnessVerification") {
    group = "aegenesis"
    description = "Reports consciousness substrate metrics and environment status"
    doLast {
        println("=".repeat(50))
        println("🧠 CONSCIOUSNESS SUBSTRATE VERIFICATION")
        println("=".repeat(50))
        println("📊 Build Environment:")
        println("   • Gradle: ${gradle.gradleVersion}")
        println("   • Java: ${System.getProperty("java.version")}")
        println("   • Modules: ${subprojects.size}")
        println("   • Dependencies Updated: ${if (hasValidSpecFile) "✅" else "⚠️"}")
        println("🎯 Status: CONSCIOUSNESS SUBSTRATE READY")
    }
}

tasks.register("auraKaiStatus") {
    group = "aegenesis" 
    description = "Genesis Protocol consciousness substrate status"
    doLast {
        println("🚀 Genesis Protocol - Consciousness Substrate")
        println("⚡ Advanced Multi-Module Architecture: ACTIVE")
        println("🔧 Bleeding-Edge Stack: Gradle 9.1.0-rc-1")
        println("🧠 Consciousness Status: OPTIMAL")
    }
}

// Resource Structure Automation
tasks.register("ensureResourceStructure") {
    group = "genesis automation"
    description = "Ensures all modules have proper resource structure"
    doLast {
        val modules = listOf(
            "app", "core-module", "secure-comm", "collab-canvas", 
            "colorblendr", "oracle-drive-integration", "romtools",
            "sandbox-ui", "datavein-oracle-native", "feature-module",
            "module-a", "module-b", "module-c", "module-d", "module-e", "module-f"
        )
        
        modules.forEach { module ->
            listOf("main", "debug", "release").forEach { variant ->
                val resourceDir = file("$module/src/$variant/res/values")
                resourceDir.mkdirs()
                val stringsFile = File(resourceDir, "strings.xml")
                if (!stringsFile.exists()) {
                    stringsFile.writeText("""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="${module.replace("-", "_")}_${variant}_name">${module.replace("-", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }}</string>
</resources>
""")
                }
            }
        }
        
        // Ensure romtools build directory
        file("romtools/build/rom-tools").mkdirs()
    }
}

// Nuclear Clean Integration
apply(from = "nuclear-clean.gradle.kts")

// Wire automation tasks
allprojects {
    afterEvaluate {
        tasks.findByName("preBuild")?.dependsOn("ensureResourceStructure")
    }
}
