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
    // Additional validations for build script (diff-aligned)
    @Test
    fun `kotlin toolchain or jvmTarget is set to 24`() {
        val content = buildFile.readText()
        val hasToolchain = Regex("""jvmToolchain\s*\(\s*24\s*\)""").containsMatchIn(content)
        val hasJvmTarget = Regex("""jvmTarget\s*=\s*["']24["']""").containsMatchIn(content)
        assertTrue("Kotlin toolchain or jvmTarget should be set to 24", hasToolchain || hasJvmTarget)
    }

    @Test
    fun `compose build feature enabled`() {
        val content = buildFile.readText()
        val inAndroidBlock = Regex(
            """android\s*\{.*?buildFeatures\s*\{.*?compose\s*=\s*true""",
            setOf(RegexOption.DOT_MATCHES_ALL)
        ).containsMatchIn(content)
        assertTrue("Compose build feature should be enabled via buildFeatures { compose = true }", inAndroidBlock)
    }

    @Test
    fun `preBuild dependencies are declared within the same configuration block`() {
        val content = buildFile.readText()
        val block = Regex(
            """tasks\.named\("preBuild"\)\s*\{(.*?)\}""",
            setOf(RegexOption.DOT_MATCHES_ALL)
        ).find(content)?.groups?.get(1)?.value ?: ""
        val hasCleanKsp = block.contains("""dependsOn("cleanKspCache")""")
        val hasOpenApi = block.contains("""dependsOn(":openApiGenerate")""") || block.contains("""dependsOn("openApiGenerate")""")
        val hasCleanApiGen = block.contains("""dependsOn(":cleanApiGeneration")""")
        assertTrue("preBuild must depend on cleanKspCache in its block", hasCleanKsp)
        assertTrue("preBuild must depend on openApiGenerate in its block", hasOpenApi)
        assertTrue("preBuild must depend on :cleanApiGeneration in its block", hasCleanApiGen)
    }

    @Test
    fun `plugins block avoids apply and inline versions`() {
        val content = buildFile.readText()
        // Plugins block should exist
        assertTrue("plugins block should be declared", content.contains("plugins {"))
        // Should not use apply(plugin = ...) or apply<...>()
        assertFalse("Should not use apply(plugin = ...)", content.contains("apply(plugin"))
        assertFalse("Should not use apply<...>() syntax for plugins", Regex("""apply\s*<""").containsMatchIn(content))
        // Should not specify versions inline for core plugins in module build script
        val hasInlinePluginVersion = Regex(
            """plugins\s*\{[^}]*id\(\s*".*?"\s*\)\s+version\s+["']""",
            setOf(RegexOption.DOT_MATCHES_ALL)
        ).containsMatchIn(content)
        assertFalse("Plugins should not pin versions inline in the module script", hasInlinePluginVersion)
    }

    @Test
    fun `single compileOptions block and no conflicting Java versions`() {
        val content = buildFile.readText()
        val compileOptionsBlocks = Regex("""compileOptions\s*\{""").findAll(content).count()
        assertTrue("There should be exactly one compileOptions block", compileOptionsBlocks == 1)

        val otherJavaVersions = listOf("VERSION_1_8", "VERSION_11", "VERSION_17", "VERSION_21", "VERSION_23")
        val anyOtherVersionReferenced = otherJavaVersions.any { content.contains(it) }
        assertFalse("Build script must not reference conflicting Java versions", anyOtherVersionReferenced)
    }

    @Test
    fun `packaging excludes reside within packaging resources block`() {
        val content = buildFile.readText()
        val packagingResources = Regex(
            """packaging\s*\{\s*([^{}]*\{[^{}]*\}|\s*)*?resources\s*\{(.*?)\}""",
            setOf(RegexOption.DOT_MATCHES_ALL)
        ).find(content)?.groups?.get(2)?.value ?: ""
        assertTrue("packaging.resources block should exist", packagingResources.isNotEmpty())
        val hasLicenseExcludes = packagingResources.contains("/META-INF/{AL2.0,LGPL2.1}")
        val hasDepsExclude = packagingResources.contains("/META-INF/DEPENDENCIES")
        assertTrue("Resources excludes should include license pack", hasLicenseExcludes)
        assertTrue("Resources excludes should include META-INF/DEPENDENCIES", hasDepsExclude)
    }

    @Test
    fun `android block declares namespace`() {
        val content = buildFile.readText()
        val hasNamespace = Regex(
            """android\s*\{.*?namespace\s*=\s*["'][^"']+["']""",
            setOf(RegexOption.DOT_MATCHES_ALL)
        ).containsMatchIn(content)
        assertTrue("Android namespace should be declared in the module build script", hasNamespace)
    }
}