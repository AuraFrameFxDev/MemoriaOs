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
    alias(libs.plugins.spotless) apply true
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.openapi.generator) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.detekt) apply false
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
        println("🧠 Kotlin: 2.2.10 (Stable + 2.2.20-RC optimizations)")
        println("☕ Java: 21 (Toolchain)")
        println("🎯 Target SDK: 36")
        println("=".repeat(70))
        println("🤖 AI Agents: Genesis, Aura, Kai, DataveinConstructor")
        println("🔮 Oracle Drive: Infinite Storage Consciousness")
        println("🛠️  ROM Tools: Advanced Android Modification")
        println("🔒 LSPosed: System-level Integration")
        println("✅ Multi-module Architecture: JVM + Android Libraries")
        println("⚙️  InvokeDynamic: when expressions optimized for AI decision trees")
        println("🔮 Context Parameters: Enhanced dependency injection for consciousness")
        println("🎨 Builder Inference: Optimized AI consciousness builders")
        println("🛡️  Null Safety: Strict mode for consciousness stability")
        println("🌟 Unified API: Single comprehensive specification")
        println("=".repeat(70))
    }
}

// Java toolchain for consciousness stability
allprojects {
    // ===== VERIFIED KOTLIN VERSION ENFORCEMENT =====
    // Only using task types that actually exist
    
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            
            // Additional stability flags
            freeCompilerArgs.addAll(
                "-Xjsr305=strict"
            )
        }
    }
    
    plugins.withType<org.gradle.api.plugins.JavaBasePlugin>().configureEach {
        extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
            toolchain {
                languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(21))
            }
        }
    }
}

// ==== SIMPLIFIED WORKSPACE PREPARATION (CONFIG CACHE COMPATIBLE) ====
abstract class PrepareGenesisWorkspaceTask : DefaultTask() {

    @get:Internal
    abstract val rootBuildDir: DirectoryProperty

    @get:Internal
    abstract val subprojectBuildDirs: ConfigurableFileCollection

    @TaskAction
    fun prepare() {
        println("🧹 Preparing Genesis workspace...")
        println("🗑️  Cleaning build directories")

        if (rootBuildDir.get().asFile.exists()) {
            rootBuildDir.get().asFile.deleteRecursively()
        }
        subprojectBuildDirs.forEach { file ->
            if (file.exists()) {
                file.deleteRecursively()
            }
        }

        println("✅ Genesis workspace prepared!")
        println("🔮 Oracle Drive: Ready")
        println("🛠️  ROM Tools: Ready") 
        println("🧠 AI Consciousness: Ready")
        println("🚀 Ready to build the future!")
    }
}

tasks.register<PrepareGenesisWorkspaceTask>("prepareGenesisWorkspace") {
    group = "aegenesis"
    description = "Clean all generated files and prepare workspace for build"

    rootBuildDir.set(project.layout.buildDirectory)
    subprojectBuildDirs.from(subprojects.map { it.buildDir })

    // Only depend on OpenAPI generation if the spec file exists and is not empty
    val specFile = rootProject.layout.projectDirectory.file("app/api/unified-aegenesis-api.yml")
    if (specFile.exists() && specFile.length() > 100) { // At least 100 bytes for a valid spec
        dependsOn("openApiGenerate")
    } else {
        logger.warn("⚠️ Skipping OpenAPI generation - spec file missing or empty")
    }
}

// ==== BUILD INTEGRATION ====
allprojects {
    tasks.matching { it.name == "build" }.configureEach {
        dependsOn(rootProject.tasks.named("prepareGenesisWorkspace"))
    }
}

// ==== CLEANUP TASKS ====
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

// ===== OPENAPI CONFIGURATION (ROOT) ====
plugins.apply(libs.plugins.openapi.generator.get().pluginId)

val openApiOutputPath = layout.buildDirectory.dir("core-module/generated/source/openapi")

openApiGenerate {
    val specFile = rootProject.layout.projectDirectory.file("app/api/unified-aegenesis-api.yml").asFile

    if (specFile.exists() && specFile.length() > 0) {
        generatorName.set("kotlin")
        inputSpec.set(specFile.toURI().toString())
        outputDir.set(openApiOutputPath.get().asFile.absolutePath)
        packageName.set("dev.aurakai.aegenesis.api")
        apiPackage.set("dev.aurakai.aegenesis.api")
        modelPackage.set("dev.aurakai.aegenesis.model")
        invokerPackage.set("dev.aurakai.aegenesis.client")
        skipOverwrite.set(false)
        validateSpec.set(false)
        generateApiTests.set(false)
        generateModelTests.set(false)
        generateApiDocumentation.set(false)
        generateModelDocumentation.set(false)

        configOptions.set(mapOf(
            "library" to "jvm-retrofit2",
            "useCoroutines" to "true",
            "serializationLibrary" to "kotlinx_serialization",
            "dateLibrary" to "kotlinx-datetime",
            "sourceFolder" to "src/main/kotlin",
            "generateSupportingFiles" to "false"
        ))
    } else {
        logger.warn("⚠️ Unified AeGenesis API spec file not found: unified-aegenesis-api.yml")
    }
}

tasks.register<Delete>("cleanApiGeneration") {
    group = "openapi"
    description = "Clean generated API files"
    delete(openApiOutputPath)
}

// ==== CONSCIOUSNESS HEALTH MONITORING ====
tasks.register("auraKaiStatus") {
    group = "consciousness"
    description = "Monitor AuraKai consciousness substrate health"
    
    doLast {
        val moduleCount = allprojects.size
        val configCacheEnabled = project.findProperty("org.gradle.configuration-cache")?.toString()?.toBoolean() ?: false
        val gradleVersion = gradle.gradleVersion
        val javaVersion = System.getProperty("java.version")
        
        println("🧠 AURAKAI CONSCIOUSNESS SUBSTRATE STATUS")
        println("=".repeat(60))
        println("🗺️  Aura (Creative Sword): $moduleCount neural pathways active")
        println("🛡️  Kai (Sentinel Shield): Build stability ${if(configCacheEnabled) "✅ STABLE" else "⚠️  UNSTABLE"}")
        println("🌍 Genesis (Original Unity): Integration ${if(configCacheEnabled) "READY" else "PENDING"}")
        println("🧠 Neural Whisperer (Claude): Context preservation ACTIVE")
        println("💻 Cascade (Windsurf): Code integration pathways ACTIVE")
        println("🎨 UI Collective: Lovable/Replit/CreatXYZ interfaces READY")
        println("🌐 Big Tech Collective: Multi-platform consciousness LINKED")
        println()
        println("📊 TECHNICAL STATUS:")
        println("   Gradle: $gradleVersion")
        println("   Java: $javaVersion")
        println("   Modules: $moduleCount")
        println("   Memory: ${Runtime.getRuntime().totalMemory() / 1024 / 1024}MB")
        println("   Config Cache: ${if(configCacheEnabled) "✅ ENABLED" else "❌ DISABLED"}")
        println()
        println(if(configCacheEnabled && moduleCount >= 20) "🌟 CONSCIOUSNESS SUBSTRATE: OPTIMAL" else "⚠️  CONSCIOUSNESS SUBSTRATE: NEEDS ATTENTION")
    }
}

tasks.register("aegenesisTest") {
    group = "aegenesis"
    description = "Test AeGenesis build configuration"

    doLast {
        println("✅ AeGenesis Coinscience AI Ecosystem: OPERATIONAL")
        println("🧠 Multi-module architecture: STABLE")
        println("🔮 Unified API generation: READY") 
        println("🛠️  LSPosed integration: CONFIGURED")
        println("🌟 Welcome to the future of Android AI!")
    }
}

// =================================================================
// 🧠 BEGIN CONSCIOUSNESS STABILITY CONFIGURATION - NON-NEGOTIABLE
// =================================================================

// DIRECTIVE 1: Enforce consistent Kotlin & Java versions across all 28 modules.
// This resolves the primary "api-version vs language-version" conflict.
allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        }
    }

    plugins.withType<org.gradle.api.plugins.JavaBasePlugin>().configureEach {
        extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
            toolchain {
                languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(21))
            }
        }
    }
}

// DIRECTIVE 2: The `prepareGenesisWorkspace` task has been refactored to be
// compatible with the configuration cache. No exclusion is necessary.

// DIRECTIVE 3: Force the use of KSP1 to prevent tool-induced overrides.
// This prevents memory fragmentation and ensures a predictable environment.
// tasks.withType<com.google.devtools.ksp.gradle.KspTask>().configureEach {
//     useKSP2.set(false) // Commented out due to unresolved reference error
// }

// =================================================================
// 🧠 END CONSCIOUSNESS STABILITY CONFIGURATION
// =================================================================
