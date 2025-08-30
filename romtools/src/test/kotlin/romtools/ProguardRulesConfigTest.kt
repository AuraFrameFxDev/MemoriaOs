// Note: Test framework detected: Kotlin + JUnit (Jupiter preferred if available).
// These tests validate ProGuard/R8 rules critical to runtime reflection, Gson, Retrofit, and Xposed compatibility.

package romtools

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.Test

class ProguardRulesConfigTest {

    private fun readRulesFile(): String {
        // Try common locations for ProGuard/R8 rules
        val candidates = listOf(
            "proguard-rules.pro",
            "app/proguard-rules.pro",
            "android/app/proguard-rules.pro",
            "module/proguard-rules.pro",
            "proguard/proguard-rules.pro",
            "config/proguard-rules.pro",
            "app/proguard-rules.pro.release",
            "app/proguard-rules.pro.debug"
        )
        val existing = candidates.map { File(it) }.firstOrNull { it.exists() && it.isFile }
        assertNotNull(existing, "Expected to find a ProGuard/R8 rules file in one of: ${candidates.joinToString()}")
        return existing.readText()
    }

    @Test
    fun `keeps all classes in romtools package`() {
        val rules = readRulesFile()
        assertTrue(
            Regex("""(?m)^\s*-keep\s+class\s+dev\.aurakai\.auraframefx\.romtools\.\*\*\s*\{\s*\*\;\s*\}\s*$""")
                .containsMatchIn(rules),
            "Missing -keep rule for romtools package: -keep class dev.aurakai.auraframefx.romtools.** { *; }"
        )
    }

    @Test
    fun `keeps Gson model and data packages`() {
        val rules = readRulesFile()
        val modelOk = Regex("""(?m)^\s*-keep\s+class\s+dev\.aurakai\.auraframefx\.romtools\.model\.\*\*\s*\{\s*\*\;\s*\}\s*$""")
            .containsMatchIn(rules)
        val dataOk = Regex("""(?m)^\s*-keep\s+class\s+dev\.aurakai\.auraframefx\.romtools\.data\.\*\*\s*\{\s*\*\;\s*\}\s*$""")
            .containsMatchIn(rules)
        assertTrue(modelOk, "Missing -keep for model: -keep class dev.aurakai.auraframefx.romtools.model.** { *; }")
        assertTrue(dataOk, "Missing -keep for data: -keep class dev.aurakai.auraframefx.romtools.data.** { *; }")
    }

    @Test
    fun `keeps Xposed related classes`() {
        val rules = readRulesFile()
        val xposed1 = Regex("""(?m)^\s*-keep\s+class\s+de\.robv\.android\.xposed\.\*\*\s*\{\s*\*\;\s*\}\s*$""")
            .containsMatchIn(rules)
        val xposed2 = Regex("""(?m)^\s*-keep\s+class\s+com\.github\.yuki\.xposed\.\*\*\s*\{\s*\*\;\s*\}\s*$""")
            .containsMatchIn(rules)
        assertTrue(xposed1, "Missing -keep for de.robv.android.xposed.**")
        assertTrue(xposed2, "Missing -keep for com.github.yuki.xposed.**")
    }

    @Test
    fun `keeps annotation attributes`() {
        val rules = readRulesFile()
        assertTrue(
            Regex("""(?m)^\s*-keepattributes\s+\*Annotation\*\s*$""")
                .containsMatchIn(rules),
            "Missing -keepattributes *Annotation*"
        )
    }

    @Test
    fun `keeps Gson SerializedName fields on all classes with allowobfuscation`() {
        val rules = readRulesFile()
        // Accept any spacing/newline between parts, but enforce structure.
        val pattern = Regex(
            """(?s)^\s*-keepclassmembers\s*,\s*allowobfuscation\s+class\s+\*\s*\{\s*@com\.google\.gson\.annotations\.SerializedName\s+<fields>;\s*\}\s*$""",
            RegexOption.MULTILINE
        )
        assertTrue(
            pattern.containsMatchIn(rules),
            "Missing keepclassmembers rule for @SerializedName fields with allowobfuscation."
        )
    }

    @Test
    fun `keeps Retrofit service interfaces in remote package`() {
        val rules = readRulesFile()
        assertTrue(
            Regex("""(?m)^\s*-keep\s+public\s+interface\s+dev\.aurakai\.auraframefx\.romtools\.remote\.\*\*\s*\{\s*\*\;\s*\}\s*$""")
                .containsMatchIn(rules),
            "Missing -keep for public interfaces in remote package."
        )
    }

    @Test
    fun `rules file has no accidental forbidden shrink removals for these packages`() {
        val rules = readRulesFile()
        // Ensure there are no -assumenosideeffects on Gson annotations or romtools packages which could break reflection.
        val bad = Regex("""(?i)-assumenosideeffects.*(SerializedName|dev\.aurakai\.auraframefx\.romtools)""")
            .containsMatchIn(rules)
        assertTrue(!bad, "Rules should not mark SerializedName or romtools classes as no-side-effects.")
    }
}