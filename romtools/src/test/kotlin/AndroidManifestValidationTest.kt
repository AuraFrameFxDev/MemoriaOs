@file:Suppress("MemberVisibilityCanBePrivate", "FunctionName")

package romtools

import org.w3c.dom.Document
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.*

class AndroidManifestValidationTest {

    private fun loadResource(path: String): InputStream {
        return requireNotNull(this::class.java.classLoader.getResourceAsStream(path)) {
            "Test resource not found on classpath: $path"
        }
    }

    private fun parseXml(resourcePath: String): Document {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            isValidating = false
        }
        loadResource(resourcePath).use { input ->
            return factory.newDocumentBuilder().parse(input)
        }
    }

    @Test
    fun `minimal manifest parses successfully`() {
        val doc = parseXml("manifests/minimal-AndroidManifest.xml")
        val root = doc.documentElement
        assertNotNull(root, "Document should have a root element")
        assertEquals("manifest", root.nodeName, "Root element must be <manifest>")
    }

    @Test
    fun `manifest has android namespace declared`() {
        val doc = parseXml("manifests/minimal-AndroidManifest.xml")
        val root = doc.documentElement
        val androidNs = root.getAttributeNode("xmlns:android")?.nodeValue
        // Fallback to lookup if attribute node retrieval differs across parsers
        val nsViaLookup = root.lookupNamespaceURI("android")
        assertTrue(
            androidNs == "http://schemas.android.com/apk/res/android" ||
            nsViaLookup == "http://schemas.android.com/apk/res/android",
            "Android namespace must be declared correctly"
        )
    }

    @Test
    fun `manifest may be empty but still valid structure`() {
        val doc = parseXml("manifests/minimal-AndroidManifest.xml")
        val applicationNodes = doc.getElementsByTagName("application")
        assertEquals(0, applicationNodes.length, "Minimal manifest should not define <application> by default")
    }

    @Test
    fun `malformed manifest fails to parse`() {
        val ex = assertFailsWith<Exception> {
            parseXml("manifests/malformed-AndroidManifest.xml")
        }
        assertTrue(
            ex.message?.contains("The element type") == true ||
            ex.cause?.message?.contains("The element type") == true,
            "Malformed XML should produce a parse error"
        )
    }

    @Test
    fun `graceful error if resource missing`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            loadResource("manifests/does-not-exist.xml")
        }
        assertTrue(ex.message!!.contains("Test resource not found"))
    }
}