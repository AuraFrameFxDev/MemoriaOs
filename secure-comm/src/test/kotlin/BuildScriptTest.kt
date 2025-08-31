// BuildScriptTest.kt — validates Gradle Kotlin DSL configuration for the secure-comm module
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNotNull
import java.io.File

/**
 * Testing library and framework:
 * - JUnit 5 (Jupiter) for test runner and assertions.
 * - Kotlin (no extra dependencies introduced).
 *
 * These tests validate the Gradle Kotlin DSL (build.gradle.kts) of the secure-comm module,
 * focusing on the PR diff. They assert presence of critical configuration, plugins,
 * Android settings, packaging excludes, build features, KSP args, and dependencies.
 *
 * Tests read the module's build.gradle.kts as text to ensure configuration remains intact.
 * This favors stability across Gradle API changes without executing builds.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildScriptTest {

    private fun readBuildFile(): String {
        // Assume tests run from the module context; resolve securely from repo root structure:
        val buildFile = File("secure-comm/build.gradle.kts")
        assertTrue(buildFile.exists(), "Expected secure-comm/build.gradle.kts to exist")
        val text = buildFile.readText()
        assertTrue(text.isNotBlank(), "Expected build.gradle.kts to be non-empty")
        return text
    }

    @Nested
    @DisplayName("Plugins configuration")
    inner class Plugins {
        @Test
        fun `includes required plugin aliases`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("plugins {"), "plugins block missing") },
                { assertTrue(txt.contains("alias(libs.plugins.android.library)"), "android.library plugin alias missing") },
                { assertTrue(txt.contains("alias(libs.plugins.kotlin.android)"), "kotlin.android plugin alias missing") },
                { assertTrue(txt.contains("alias(libs.plugins.kotlin.serialization)"), "kotlin.serialization alias missing") },
                { assertTrue(txt.contains("alias(libs.plugins.ksp)"), "ksp alias missing") },
                { assertTrue(txt.contains("alias(libs.plugins.hilt)"), "hilt alias missing") },
                { assertTrue(txt.contains("alias(libs.plugins.dokka)"), "dokka alias missing") },
                { assertTrue(txt.contains("alias(libs.plugins.spotless)"), "spotless alias missing") },
                { assertTrue(txt.contains("alias(libs.plugins.kover)"), "kover alias missing") },
            )
        }
    }

    @Nested
    @DisplayName("KSP configuration")
    inner class KspConfig {
        @Test
        fun `uses Kotlin 2_2 for language and api versions`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("ksp {"), "ksp block missing") },
                { assertTrue(txt.contains("arg(\"kotlin.languageVersion\", \"2.2\")"), "ksp kotlin.languageVersion not set to 2.2") },
                { assertTrue(txt.contains("arg(\"kotlin.apiVersion\", \"2.2\")"), "ksp kotlin.apiVersion not set to 2.2") },
            )
        }
    }

    @Nested
    @DisplayName("Android block")
    inner class AndroidBlock {
        @Test
        fun `has expected namespace and SDKs`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("android {"), "android block missing") },
                { assertTrue(txt.contains("namespace = \"dev.aurakai.auraframefx.securecomm\""), "namespace is incorrect or missing") },
                { assertTrue(Regex("""compileSdk\s*=\s*36""").containsMatchIn(txt), "compileSdk must be 36") },
               { assertTrue(Regex("""minSdk\s*=\s*23""").containsMatchIn(txt), "minSdk must be 23") },
                { assertTrue(txt.contains("testInstrumentationRunner = \"androidx.test.runner.AndroidJUnitRunner\""),
                    "testInstrumentationRunner must be AndroidJUnitRunner") },
                { assertTrue(txt.contains("consumerProguardFiles(\"consumer-rules.pro\")"),
                    "consumerProguardFiles must include consumer-rules.pro") },
            )
        }

        @Test
        fun `release build type uses minify and proguard files`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("buildTypes {"), "buildTypes block missing") },
                { assertTrue(Regex("""release\s*\{""").containsMatchIn(txt), "release buildType missing") },
                { assertTrue(Regex("""isMinifyEnabled\s*=\s*true""").containsMatchIn(txt), "release isMinifyEnabled must be true") },
                { assertTrue(txt.contains("getDefaultProguardFile(\"proguard-android-optimize.txt\")"),
                    "Default optimized proguard file should be included") },
                { assertTrue(txt.contains("\"proguard-rules.pro\""),
                    "Module-specific proguard-rules.pro should be included") },
            )
        }

        @Test
        fun `build features explicitly configured`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("buildFeatures {"), "buildFeatures block missing") },
                { assertTrue(Regex("""compose\s*=\s*false""").containsMatchIn(txt), "compose should be disabled") },
                { assertTrue(Regex("""buildConfig\s*=\s*true""").containsMatchIn(txt), "buildConfig should be enabled") },
                { assertTrue(Regex("""viewBinding\s*=\s*false""").containsMatchIn(txt), "viewBinding should be disabled") },
            )
        }

        @Test
        fun `packaging excludes critical META-INF artifacts`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("packaging {"), "packaging block missing") },
                { assertTrue(txt.contains("resources {"), "packaging.resources block missing") },
                { assertTrue(txt.contains("excludes += listOf("), "excludes list missing") },
                { assertTrue(txt.contains("\"/META-INF/{AL2.0,LGPL2.1}\""), "missing AL2.0,LGPL2.1 exclude") },
                { assertTrue(txt.contains("\"/META-INF/DEPENDENCIES\""), "missing DEPENDENCIES exclude") },
                { assertTrue(txt.contains("\"/META-INF/LICENSE\""), "missing LICENSE exclude") },
                { assertTrue(txt.contains("\"/META-INF/LICENSE.txt\""), "missing LICENSE.txt exclude") },
                { assertTrue(txt.contains("\"/META-INF/NOTICE\""), "missing NOTICE exclude") },
                { assertTrue(txt.contains("\"/META-INF/NOTICE.txt\""), "missing NOTICE.txt exclude") },
                { assertTrue(txt.contains("\"META-INF/*.kotlin_module\""), "missing kotlin_module wildcard exclude") },
            )
        }
    }

    @Nested
    @DisplayName("Dependencies")
    inner class DependenciesBlock {
        @Test
        fun `core project and Android libs present`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("dependencies {"), "dependencies block missing") },
                { assertTrue(txt.contains("implementation(project(\":core-module\"))"), "missing core-module dependency") },
                { assertTrue(txt.contains("implementation(libs.androidx.core.ktx)"), "missing androidx.core.ktx") },
                { assertTrue(txt.contains("implementation(libs.androidx.appcompat)"), "missing androidx.appcompat") },
            )
        }

        @Test
        fun `kotlin libraries configured`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("implementation(libs.kotlin.stdlib)"), "missing kotlin stdlib") },
                { assertTrue(txt.contains("implementation(libs.kotlin.reflect)"), "missing kotlin reflect") },
                { assertTrue(txt.contains("implementation(libs.bundles.coroutines)"), "missing coroutines bundle") },
                { assertTrue(txt.contains("implementation(libs.kotlinx.serialization.json)"), "missing kotlinx.serialization json") },
            )
        }

        @Test
        fun `hilt and ksp wiring is complete for all source sets`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("implementation(libs.hilt.android)"), "missing hilt android") },
                { assertTrue(txt.contains("ksp(libs.hilt.compiler)"), "missing ksp hilt compiler (main)") },
                { assertTrue(txt.contains("androidTestImplementation(libs.hilt.android.testing)"), "missing androidTest hilt testing") },
                { assertTrue(txt.contains("kspAndroidTest(libs.hilt.compiler)"), "missing ksp hilt compiler (androidTest)") },
                { assertTrue(txt.contains("testImplementation(libs.hilt.android.testing)"), "missing unit test hilt testing") },
                { assertTrue(txt.contains("kspTest(libs.hilt.compiler)"), "missing ksp hilt compiler (test)") },
            )
        }

        @Test
        fun `networking stack present`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("implementation(libs.retrofit)"), "missing retrofit") },
                { assertTrue(txt.contains("implementation(libs.retrofit.converter.kotlinx.serialization)"), "missing retrofit kotlinx converter") },
                { assertTrue(txt.contains("implementation(libs.okhttp3.logging.interceptor)"), "missing okhttp logging interceptor") },
            )
        }

        @Test
        fun `security and utilities present`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("implementation(libs.androidxSecurity)"), "missing androidx security") },
                { assertTrue(txt.contains("implementation(libs.bouncycastle)"), "missing bouncycastle") },
                { assertTrue(txt.contains("implementation(libs.gson)"), "missing gson") },
                { assertTrue(txt.contains("implementation(libs.commons.io)"), "missing commons-io") },
                { assertTrue(txt.contains("implementation(libs.commons.compress)"), "missing commons-compress") },
                { assertTrue(txt.contains("implementation(libs.xz)"), "missing xz") },
            )
        }

        @Test
        fun `test dependencies aligned to JUnit Jupiter and coroutines`() {
            val txt = readBuildFile()
            assertAll(
                { assertTrue(txt.contains("testImplementation(libs.junit)"), "missing junit api (BOM or api)") },
                { assertTrue(txt.contains("testImplementation(libs.junit.jupiter)"), "missing junit jupiter api") },
                { assertTrue(txt.contains("testRuntimeOnly(libs.junit.engine)"), "missing junit engine runtime") },
                { assertTrue(txt.contains("testImplementation(libs.mockk)"), "missing mockk") },
                { assertTrue(txt.contains("testImplementation(libs.turbine)"), "missing turbine") },
                { assertTrue(txt.contains("testImplementation(libs.kotlinx.coroutines.test)"), "missing coroutines-test") },
                { assertTrue(txt.contains("androidTestImplementation(libs.androidx.test.ext.junit)"), "missing androidx junit ext for androidTest") },
                { assertTrue(txt.contains("androidTestImplementation(libs.androidx.test.core)"), "missing androidx test core for androidTest") },
            )
        }
    }

    @Nested
    @DisplayName("Defensive checks and regressions")
    inner class Defensive {
        @Test
        fun `file does not accidentally enable compose or viewBinding`() {
            val txt = readBuildFile()
            // Ensure no stray enablements slipped through; the explicit checks above already assert exact values.
            assertTrue(!Regex("""compose\s*=\s*true""").containsMatchIn(txt), "compose should not be enabled")
            assertTrue(!Regex("""viewBinding\s*=\s*true""").containsMatchIn(txt), "viewBinding should not be enabled")
        }

        @Test
        fun `proguard configuration present only in release`() {
            val txt = readBuildFile()
            // Rough heuristic: ensure no debug minify enabling.
            assertTrue(!Regex("""debug\s*\{[^}]*isMinifyEnabled\s*=\s*true""", RegexOption.DOT_MATCHES_ALL).containsMatchIn(txt),
                "debug build should not enable minify")
        }
    }
}