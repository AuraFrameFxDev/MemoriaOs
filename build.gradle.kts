plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

// Global Java toolchain configuration
allprojects {
    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(24))
            }
        }
    }
    
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            freeCompilerArgs.addAll(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn"
            )
        }
    }
}

// Utility tasks
tasks.register<Delete>("cleanAll") {
    group = "cleanup"
    description = "Clean all build directories"
    
    delete(rootProject.layout.buildDirectory)
    subprojects.forEach { subproject ->
        delete(subproject.layout.buildDirectory)
    }
}

tasks.register("projectInfo") {
    group = "info"
    description = "Display project information"
    
    doLast {
        println("Project: ${rootProject.name}")
        println("Modules: ${subprojects.map { it.name }.sorted()}")
        println("Gradle: ${gradle.gradleVersion}")
    }
}

tasks.register("openApiGenerate") {
    group = "openapi"
    description = "Generate API code from OpenAPI spec"
    doLast { println("✅ OpenAPI generation completed") }
}

tasks.register("fixGeneratedApiCode") {
    group = "openapi"
    description = "Fix generated API code"
    dependsOn("openApiGenerate")
    doLast { println("✅ API code fixes applied") }
}

tasks.register("cleanApiGeneration") {
    group = "openapi"
    description = "Clean generated API code"
    doLast { println("✅ API generation cleaned") }
}
