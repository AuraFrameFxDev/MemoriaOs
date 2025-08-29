import io.mockk.clearAllMocks
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class BuildScriptsValidationV2Test {

    private lateinit var buildFile: File

    @BeforeEach
    fun setup() {
        buildFile = File("app/build.gradle.kts")
        assertTrue("Build script should exist for validation", buildFile.exists())
    }

    @AfterEach
    fun teardown() {
        clearAllMocks()
    }

    // Focus: Recent build script changes (diff-aligned)
    @Test
    fun `uses plugin ids for core plugins (no version-catalog aliasing)`() {
        val content = buildFile.readText()

        // Verify plugin IDs are applied
        assertTrue(content.contains("""id("com.android.application")"""))
        assertTrue(content.contains("""id("org.jetbrains.kotlin.android")"""))
        assertTrue(content.contains("""id("org.jetbrains.kotlin.plugin.compose")"""))
        assertTrue(content.contains("""id("org.jetbrains.kotlin.plugin.serialization")"""))
        assertTrue(content.contains("""id("com.google.devtools.ksp")"""))

        // Ensure the alias-style plugin application isn't used in the app module (migration to id())
        assertFalse("Should not use version-catalog alias for Android application plugin",
            content.contains("alias(libs.plugins.androidApplication)"))
        assertFalse("Should not use version-catalog alias for Kotlin Android plugin",
            content.contains("alias(libs.plugins.kotlinAndroid)"))
        assertFalse("Should not use version-catalog alias for KSP plugin",
            content.contains("alias(libs.plugins.ksp)"))
    }

    @Test
    fun `compile options are set to Java 24`() {
        val content = buildFile.readText()
        assertTrue("Source compatibility should be Java 24",
            content.contains("sourceCompatibility = JavaVersion.VERSION_24"))
        assertTrue("Target compatibility should be Java 24",
            content.contains("targetCompatibility = JavaVersion.VERSION_24"))
    }

    @Test
    fun `preBuild task depends on OpenAPI generation and cleanup tasks`() {
        val content = buildFile.readText()

        // Ensure preBuild hook is present
        assertTrue("preBuild task hook should be declared",
            content.contains("""tasks.named("preBuild")"""))

        // Ensure dependsOn wiring is present (accept both path-qualified and unqualified OpenAPI task)
        val hasOpenApiDepends =
            content.contains("""dependsOn(":openApiGenerate")""") || content.contains("""dependsOn("openApiGenerate")""")
        assertTrue("preBuild should depend on OpenAPI generation", hasOpenApiDepends)

        assertTrue("preBuild should depend on cleaning KSP cache",
            content.contains("""dependsOn("cleanKspCache")"""))
        assertTrue("preBuild should depend on API generation cleanup",
            content.contains("""dependsOn(":cleanApiGeneration")"""))
    }

    @Test
    fun `packaging resources and jniLibs are configured correctly`() {
        val content = buildFile.readText()

        // Resources excludes
        assertTrue("Should configure packaging resources block", content.contains("packaging {"))
        assertTrue("Should configure resources excludes", content.contains("resources {"))
        assertTrue("Should exclude AL2.0 and LGPL2.1 license files",
            content.contains("""excludes += setOf(""") &&
            content.contains("/META-INF/{AL2.0,LGPL2.1}"))
        assertTrue("Should exclude META-INF/DEPENDENCIES",
            content.contains("/META-INF/DEPENDENCIES"))

        // JNI libs packaging
        assertTrue("Should configure jniLibs packaging", content.contains("jniLibs {"))
        assertTrue("Should set useLegacyPackaging = false for JNI libs",
            content.contains("useLegacyPackaging = false"))
        assertTrue("Should pickFirst libc++_shared.so",
            content.contains("**/libc++_shared.so"))
        assertTrue("Should pickFirst libjsc.so",
            content.contains("**/libjsc.so"))
    }

    @Test
    fun `single preBuild hook declaration`() {
        val content = buildFile.readText()
        val preBuildBlocks = Regex("""tasks\.named\("preBuild"\)\s*\{""").findAll(content).count()
        assertTrue("There should be exactly one preBuild task configuration block", preBuildBlocks == 1)
    }
}