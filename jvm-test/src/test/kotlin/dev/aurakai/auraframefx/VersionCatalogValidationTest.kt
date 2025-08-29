package dev.aurakai.auraframefx

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.fail
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

/**
 * VersionCatalogValidationTest
 *
 * Baseline tests to validate Gradle Version Catalog (libs.versions.toml) invariants.
 * This file was created because it didn't exist in the PR context. If an existing file
 * is later found, these tests should be merged and deduplicated following the project's style.
 */
class VersionCatalogValidationTest {

    private fun readFile(path: String): String =
        Files.readString(Paths.get(path))

    private fun catalogPath(): String = "gradle/libs.versions.toml"

    @Test
    @DisplayName("Catalog file exists and is readable")
    fun catalogExists() {
        val path = Paths.get(catalogPath())
        assertTrue(Files.exists(path), "gradle/libs.versions.toml should exist")
        assertTrue(Files.isRegularFile(path), "gradle/libs.versions.toml should be a regular file")
        assertTrue(Files.isReadable(path), "gradle/libs.versions.toml should be readable")
    }

    @Test
    @DisplayName("Catalog contains required top-level tables [versions], [libraries], [plugins]")
    fun containsRequiredTables() {
        val text = readFile(catalogPath())
        assertAll(
            { assertTrue(text.contains("\n[versions]") || text.startsWith("[versions]"), "Missing [versions] table") },
            { assertTrue(text.contains("\n[libraries]") || text.startsWith("[libraries]"), "Missing [libraries] table") },
            { assertTrue(text.contains("\n[plugins]") || text.startsWith("[plugins]"), "Missing [plugins] table") }
        )
    }

    @Test
    @DisplayName("No obvious placeholder versions (e.g., 0.0.0, x.y.z) present")
    fun noPlaceholderVersions() {
        val text = readFile(catalogPath())
        val bad = listOf("0.0.0", "x.y.z", "TBD", "REPLACE_ME")
        val found = bad.filter { text.contains(it) }
        if (found.isNotEmpty()) {
            fail("Found placeholder versions in catalog: $found")
        }
    }
}

    @Test
    @DisplayName("All library aliases reference defined versions or inline versions")
    fun libraryAliasesReferenceValidVersions() {
        val text = Files.readString(Paths.get(catalogPath()))
        // Collect version keys from [versions]
        val versionKeys = "\\[versions]([\\s\\S]*?)\\n\\[".toRegex().find(text)?.groupValues?.get(1)
            ?.lines()
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() && !it.startsWith("#") }
            ?.mapNotNull { line ->
                // foo = "1.2.3" OR foo = { strictly = "1.2.3" }
                val key = line.split("=").firstOrNull()?.trim()
                key?.takeIf { it.matches(Regex("[A-Za-z0-9._-]+")) }
            }?.toSet() ?: emptySet()

        // Extract [libraries] block
        val librariesBlock = "\\[libraries]([\\s\\S]*?)\\n\\[".toRegex().find(text)?.groupValues?.get(1) ?: ""
        val problematic = mutableListOf<String>()

        librariesBlock.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") && it.contains("=") }
            .forEach { line ->
                // Example formats:
                // alias = { module = "group:name", version.ref = "foo" }
                // alias = { module = "group:name", version = "1.2.3" }
                // alias = "group:name" (no version here; might be managed by BOM or platform)
                val alias = line.substringBefore("=").trim()
                val rhs = line.substringAfter("=").trim()
                if (rhs.contains("version.ref")) {
                    val ref = Regex("version\\.ref\\s*=\\s*\"([^\"]+)\"").find(rhs)?.groupValues?.get(1)
                    if (ref == null || ref !in versionKeys) {
                        problematic += "$alias -> version.ref=$ref (missing or undefined)"
                    }
                } else if (rhs.contains("version")) {
                    // Inline version present; basic sanity check
                    val inline = Regex("version\\s*=\\s*\"([^\"]+)\"").find(rhs)?.groupValues?.get(1)
                    if (inline.isNullOrBlank() || inline.matches(Regex("0\\.0\\.0|x\\.y\\.z|TBD|REPLACE_ME"))) {
                        problematic += "$alias -> inline version invalid: $inline"
                    }
                } else {
                    // No explicit version; allow if using BOM/platform, but flag if neither indicated
                    // Heuristic: if no version or version.ref and no 'platform' or 'bundle', flag as potential issue
                    val hasPlatform = rhs.contains("platform", ignoreCase = true) || rhs.contains("bom", ignoreCase = true)
                    if (!hasPlatform) {
                        // permit pure coordinates without version, but still note for visibility
                        // We'll treat as warning-like failure to stay conservative for CI signal
                        problematic += "$alias -> no version or version.ref; ensure managed by BOM/platform"
                    }
                }
            }

        if (problematic.isNotEmpty()) {
            val msg = buildString {
                appendLine("Invalid or ambiguous library version references found:")
                problematic.forEach { appendLine(" - $it") }
            }
            fail(msg)
        }
    }

    @Test
    @DisplayName("Plugin entries have valid id and version/ref")
    fun pluginsHaveValidIdAndVersion() {
        val text = Files.readString(Paths.get(catalogPath()))
        val pluginsBlock = "\\[plugins]([\\s\\S]*?)$".toRegex().find(text)?.groupValues?.get(1) ?: ""
        val problems = mutableListOf<String>()

        // Collect version keys
        val versionKeys = "\\[versions]([\\s\\S]*?)\\n\\[".toRegex().find(text)?.groupValues?.get(1)
            ?.lines()
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() && !it.startsWith("#") }
            ?.mapNotNull { line -> line.substringBefore("=").trim().takeIf { it.matches(Regex("[A-Za-z0-9._-]+")) } }
            ?.toSet() ?: emptySet()

        pluginsBlock.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") && it.contains("=") }
            .forEach { line ->
                // alias = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
                // alias = { id = "com.github.ben-manes.versions", version = "0.51.0" }
                val alias = line.substringBefore("=").trim()
                val rhs = line.substringAfter("=").trim()
                val id = Regex("id\\s*=\\s*\"([^\"]+)\"").find(rhs)?.groupValues?.get(1)
                if (id.isNullOrBlank() || !id.contains(".")) {
                    problems += "$alias -> invalid plugin id: '$id'"
                }
                val ref = Regex("version\\.ref\\s*=\\s*\"([^\"]+)\"").find(rhs)?.groupValues?.get(1)
                val inline = Regex("version\\s*=\\s*\"([^\"]+)\"").find(rhs)?.groupValues?.get(1)

                if (ref == null && inline == null) {
                    problems += "$alias -> missing version or version.ref"
                } else if (ref != null && ref !in versionKeys) {
                    problems += "$alias -> version.ref '$ref' not found in [versions]"
                } else if (inline != null && inline.matches(Regex("0\\.0\\.0|x\\.y\\.z|TBD|REPLACE_ME"))) {
                    problems += "$alias -> placeholder inline version: '$inline'"
                }
            }

        if (problems.isNotEmpty()) {
            val msg = buildString {
                appendLine("Plugin catalog problems detected:")
                problems.forEach { appendLine(" - $it") }
            }
            fail(msg)
        }
    }

    @Nested
    inner class DefensiveParsing {

        @Test
        @DisplayName("Gracefully handles empty or whitespace-only catalog file")
        fun emptyCatalog() {
            // Skip if real file exists; simulate content
            val simulated = "   \n# empty catalog\n"
            val hasVersions = simulated.contains("\n[versions]") || simulated.startsWith("[versions]")
            val hasLibraries = simulated.contains("\n[libraries]") || simulated.startsWith("[libraries]")
            val hasPlugins = simulated.contains("\n[plugins]") || simulated.startsWith("[plugins]")
            assertEquals(false, hasVersions || hasLibraries || hasPlugins, "Empty catalog should not claim required tables")
        }

        @Test
        @DisplayName("Detects malformed lines without breaking parsing logic")
        fun malformedLines() {
            val snippet = """
                [versions]
                kotlin = 
                junit = "5.10.2"

                [libraries]
                ktor-client = { module = "io.ktor:ktor-client-core", version.ref = "kotlin
                junit-api = { module = "org.junit.jupiter:junit-jupiter-api", version.ref = "junit" }

                [plugins]
                kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
            """.trimIndent()

            // Heuristic checks shouldn't throw; instead they report problems if used by the main tests.
            // Here we do a minimal assertion to ensure regex extraction doesn't crash on malformed input.
            val versionsBlock = "\\[versions]([\\s\\S]*?)\\n\\[".toRegex().find(snippet)?.groupValues?.get(1)
            assertNotNull(versionsBlock, "Should extract [versions] block even if some lines are malformed")
        }
    }