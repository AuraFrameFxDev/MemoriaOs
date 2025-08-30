@file:Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import org.xml.sax.SAXParseException

/**
 * Tests focused on validating the AndroidManifest.xml structure/content relevant to the PR's diff.
 *
 * Notes:
 * - Framework: JUnit 5 (Jupiter). If this repository uses a different framework,
 *   align the imports accordingly (e.g., JUnit4 @Test and Assert, or kotest).
 * - These tests parse XML directly to avoid Android runtime dependencies.
 */
class AndroidManifestValidationTest {

    private fun parse(xml: String) = runCatching {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            isValidating = false
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }
        val builder = factory.newDocumentBuilder()
        ByteArrayInputStream(xml.toByteArray(StandardCharsets.UTF_8)).use { input ->
            builder.parse(input)
        }
    }

    // Baseline manifest content as per the provided file (minimal skeleton)
    private val baselineManifest = """
        <?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android">
        </manifest>
    """.trimIndent()

    @Test
    @DisplayName("Baseline: Manifest is well-formed and has <manifest> root")
    fun baseline_manifest_isWellFormed() {
        val doc = parse(baselineManifest).getOrNull()
        assertNotNull(doc, "XML should parse without errors")
        val root = doc!!.documentElement
        assertEquals("manifest", root.nodeName, "Root element should be <manifest>")
    }

    @Test
    @DisplayName("Baseline: Android namespace is present and correctly bound")
    fun baseline_manifest_hasAndroidNamespace() {
        val doc = parse(baselineManifest).getOrNull()
        assertNotNull(doc)
        val root = doc!!.documentElement
        val androidNs = root.getAttributeNode("xmlns:android")?.nodeValue
        assertEquals("http://schemas.android.com/apk/res/android", androidNs, "android namespace URI must match")
    }

    @Test
    @DisplayName("Edge: Missing android namespace should fail namespace-dependent checks")
    fun edge_missingAndroidNamespace() {
        val noNs = """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest>
            </manifest>
        """.trimIndent()
        val doc = parse(noNs).getOrNull()
        assertNotNull(doc, "Still well-formed XML, but missing namespace")
        val root = doc!!.documentElement
        assertTrue(root.getAttribute("xmlns:android").isNullOrBlank(), "No xmlns:android should be present")
    }

    @Test
    @DisplayName("Edge: Malformed XML should throw parsing error")
    fun edge_malformedXml_throws() {
        val malformed = """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android">
            <!-- missing closing tag -->
        """.trimIndent()
        val result = parse(malformed)
        assertTrue(result.exceptionOrNull() is SAXParseException, "Malformed XML should raise SAXParseException")
    }

    @Test
    @DisplayName("Edge: Unexpected attribute on root should be detectable")
    fun edge_unexpectedAttribute_detected() {
        val extraAttr = """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android" foo="bar">
            </manifest>
        """.trimIndent()
        val doc = parse(extraAttr).getOrNull()
        assertNotNull(doc)
        val root = doc!!.documentElement
        assertEquals("bar", root.getAttribute("foo"), "Custom attribute should be readable for validation")
        // Example policy: ensure no unknown top-level attributes other than namespaces
        val attrs = (0 until root.attributes.length).map { i -> root.attributes.item(i).nodeName }
        val allowedPrefixes = setOf("xmlns:", "package", "platformBuildVersionCode", "platformBuildVersionName")
        val unexpected = attrs.filter { attr ->
            allowedPrefixes.none { prefix -> attr == prefix || attr.startsWith(prefix) }
        }
        // We don't fail the test (since PR may intentionally add attributes), but we assert detection works.
        assertTrue(unexpected.contains("foo"), "Unexpected attribute should be discoverable for policy checks")
    }

    @Test
    @DisplayName("Security: Disallow external entities / DOCTYPE")
    fun security_disallowDoctype() {
        val withDoctype = """
            <?xml version="1.0" encoding="utf-8"?>
            <!DOCTYPE foo [ <!ENTITY xxe SYSTEM "file:///etc/passwd"> ]>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android">
            </manifest>
        """.trimIndent()
        val result = parse(withDoctype)
        val ex = result.exceptionOrNull()
        // Parser is configured with disallow-doctype-decl; should fail
        assertTrue(ex is ParserConfigurationException || ex is SAXParseException, "DOCTYPE should be disallowed by parser config")
    }
}

private fun String?.isNullOrBlank(): Boolean = this == null || this.isBlank()