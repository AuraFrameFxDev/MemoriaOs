package dev.aurakai.romtools

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

/**
 * Test suite that validates critical ProGuard/R8 rules from the PR diff.
 *
 * Testing framework: JUnit (Kotlin + JUnit assertions).
 *
 * Strategy:
 * - Discover .pro rules files under the 'romtools' module (e.g., proguard-rules.pro, rules.pro).
 * - Normalize content by trimming whitespace and ignoring comment lines.
 * - Assert that essential rules from the diff are present to protect key APIs and frameworks.
 *
 * These tests help catch regressions where important -keep rules are accidentally removed or renamed.
 */
class ProguardRulesTest {

    private fun moduleRoot(): Path {
        // Attempt common execution roots:
        // Gradle 'test' usually sets working dir to module dir; if not, fall back to repo root + /romtools
        val cwd = Paths.get(System.getProperty("user.dir"))
        val romtoolsAtCwd = cwd.resolve("src").resolve("main")
        if (Files.exists(romtoolsAtCwd)) {
            // Looks like we're already inside romtools module
            return cwd
        }
        val repoRomtools = cwd.resolve("romtools")
        if (Files.exists(repoRomtools)) {
            return repoRomtools
        }
        // Try one level up (some CI runners start inside build dir)
        val parent = cwd.parent ?: cwd
        val parentRomtools = parent.resolve("romtools")
        if (Files.exists(parentRomtools)) {
            return parentRomtools
        }
        // Last resort: assume current dir
        return cwd
    }

    private fun findRulesFiles(): List<Path> {
        val root = moduleRoot()
        val candidates = mutableListOf<Path>()
        Files.walk(root).use { stream ->
            stream.filter { it.isRegularFile() }
                .filter { p ->
                    val n = p.name.lowercase()
                    (n.endsWith(".pro") && (n.contains("proguard") || n.contains("rules"))) ||
                        n == "proguard-rules.pro"
                }
                .forEach { candidates += it }
        }
        return candidates
    }

    private fun readNormalized(file: Path): List<String> {
        val lines = Files.readAllLines(file)
        return lines.asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filter { !it.startsWith("#") }
            .toList()
    }

    private fun allRulesContent(): String {
        val files = findRulesFiles()
        if (files.isEmpty()) {
            fail("No ProGuard/R8 rules files were found under module '${moduleRoot()}'. Expected e.g. proguard-rules.pro or *rules*.pro")
        }
        val joined = files.flatMap { readNormalized(it) }
        return joined.joinToString("\n")
    }

    @Test
    fun `rules file exists and is not empty`() {
        val files = findRulesFiles()
        assertTrue(
            "Expected at least one rules file under '${moduleRoot()}'.",
            files.isNotEmpty()
        )
        val nonEmpty = files.filter { Files.size(it) > 0 }
        assertTrue(
            "Found rules files, but all appear empty: ${files.joinToString()}",
            nonEmpty.isNotEmpty()
        )
    }

    @Test
    fun `keeps romtools package classes`() {
        val content = allRulesContent()
        // From diff: -keep class dev.aurakai.auraframefx.romtools.** { *; }
        assertTrue(
            "Missing keep rule for romtools package classes: '-keep class dev.aurakai.auraframefx.romtools.** { *; }'",
            Regex("""(?m)^-keep\s+class\s+dev\.aurakai\.auraframefx\.romtools\.\*\*\s*\{\s*\*\s*;\s*\}\s*$""").containsMatchIn(content)
        )
    }

    @Test
    fun `keeps Gson model and data packages`() {
        val content = allRulesContent()

        val modelRule = Regex("""(?m)^-keep\s+class\s+dev\.aurakai\.auraframefx\.romtools\.model\.\*\*\s*\{\s*\*\s*;\s*\}\s*$""")
        val dataRule = Regex("""(?m)^-keep\s+class\s+dev\.aurakai\.auraframefx\.romtools\.data\.\*\*\s*\{\s*\*\s*;\s*\}\s*$""")

        assertTrue(
            "Missing keep rule for Gson model classes: '-keep class dev.aurakai.auraframefx.romtools.model.** { *; }'",
            modelRule.containsMatchIn(content)
        )
        assertTrue(
            "Missing keep rule for Gson data classes: '-keep class dev.aurakai.auraframefx.romtools.data.** { *; }'",
            dataRule.containsMatchIn(content)
        )
    }

    @Test
    fun `keeps Xposed related classes`() {
        val content = allRulesContent()

        val xposedRule1 = Regex("""(?m)^-keep\s+class\s+de\.robv\.android\.xposed\.\*\*\s*\{\s*\*\s*;\s*\}\s*$""")
        val xposedRule2 = Regex("""(?m)^-keep\s+class\s+com\.github\.yuki\.xposed\.\*\*\s*\{\s*\*\s*;\s*\}\s*$""")

        assertTrue(
            "Missing keep rule for de.robv.android.xposed.**",
            xposedRule1.containsMatchIn(content)
        )
        assertTrue(
            "Missing keep rule for com.github.yuki.xposed.**",
            xposedRule2.containsMatchIn(content)
        )
    }

    @Test
    fun `keeps annotations via keepattributes`() {
        val content = allRulesContent()
        assertTrue(
            "Missing annotation keepattributes rule: '-keepattributes *Annotation*'",
            Regex("""(?m)^-keepattributes\s+\*Annotation\*\s*$""").containsMatchIn(content)
        )
    }

    @Test
    fun `keeps Gson SerializedName fields on all classes`() {
        val content = allRulesContent()
        // From diff block:
        // -keepclassmembers,allowobfuscation class * {
        //     @com.google.gson.annotations.SerializedName <fields>;
        // }
        val ruleBlock = Regex(
            pattern = """(?s)-keepclassmembers,allowobfuscation\s+class\s+\*\s*\{\s*@com\.google\.gson\.annotations\.SerializedName\s+<fields>;\s*\}""",
            options = setOf(RegexOption.MULTILINE)
        )
        assertTrue(
            "Missing keepclassmembers rule for @SerializedName fields.",
            ruleBlock.containsMatchIn(content)
        )
    }

    @Test
    fun `keeps Retrofit service interfaces in remote package`() {
        val content = allRulesContent()
        assertTrue(
            "Missing keep rule for Retrofit service interfaces: '-keep public interface dev.aurakai.auraframefx.romtools.remote.** { *; }'",
            Regex("""(?m)^-keep\s+public\s+interface\s+dev\.aurakai\.auraframefx\.romtools\.remote\.\*\*\s*\{\s*\*\s*;\s*\}\s*$""")
                .containsMatchIn(content)
        )
    }

    @Test
    fun `rules file does not accidentally strip all annotations`() {
        val content = allRulesContent()
        // Quick sanity: ensure there is no rule that would remove all annotations broadly
        val dangerous = Regex("""(?m)^-dontnote\s+annotations$|^-dontwarn\s+annotations$""")
        assertTrue(
            "Found potentially dangerous rule stripping note/warn for annotations.",
            !dangerous.containsMatchIn(content)
        )
    }
}