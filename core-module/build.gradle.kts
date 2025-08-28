plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dokka)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover)
    alias(libs.plugins.openapi.generator)
}

android {
    namespace = "dev.aurakai.auraframefx.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("build/generated/source/openapi/src/main/kotlin")
        }
    }
}

dependencies {
    // Core Kotlin libraries
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.bundles.coroutines)
    implementation(libs.kotlinx.serialization.json)

    // Networking (for the generated Retrofit client)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp3.logging.interceptor)

    // Utilities
    implementation(libs.gson)

    // Security
    implementation(libs.bouncycastle)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

// ===== OPENAPI CONFIGURATION =====
val outputPath = layout.buildDirectory.dir("generated/source/openapi")

// Configure the single unified API generation
openApiGenerate {
    val specFile = rootProject.layout.projectDirectory.file("app/api/unified-aegenesis-api.yml").asFile

    if (specFile.exists() && specFile.length() > 0) {
        generatorName.set("kotlin")
        inputSpec.set(specFile.toURI().toString())
        outputDir.set(outputPath.get().asFile.absolutePath)
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
    delete(outputPath)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("openApiGenerate")
}
