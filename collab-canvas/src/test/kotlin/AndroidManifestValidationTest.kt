// NOTE: Test framework detected: $([ $JUNIT5 -eq 1 ] && echo "JUnit 5 (Jupiter)" || echo "JUnit 4")
// Robolectric detected: $([ $ROBO -eq 1 ] && echo "Yes" || echo "No")
package collab.canvas.manifest

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element



class AndroidManifestValidationTest {

    private val manifestFile = File("collab-canvas/src/main/AndroidManifest.xml")

    private fun parseManifest(): org.w3c.dom.Document {
        require(manifestFile.exists()) { "AndroidManifest.xml was not found at: ${manifestFile.absolutePath}" }
        val dbf = DocumentBuilderFactory.newInstance()
        // Harden parser options for security and stability
        dbf.isExpandEntityReferences = false
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false)
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        val db = dbf.newDocumentBuilder()
        return db.parse(manifestFile)
    }

    @Test
    fun manifest_exists_and_is_well_formed() {
        val doc = parseManifest()
        assertNotNull(doc.documentElement)
        assertEquals("manifest", doc.documentElement.tagName)
        // Namespace check
        val ns = doc.documentElement.getAttribute("xmlns:android")
        assertTrue("Android namespace not declared", ns == "http://schemas.android.com/apk/res/android" || ns.isNotBlank())
    }

    @Test
    fun manifest_declares_package_attribute() {
        val doc = parseManifest()
        val root = doc.documentElement
        val pkg = root.getAttribute("package")
        // Some Gradle setups use "namespace" in build.gradle; package may be omitted. We allow either.
        val hasPackage = pkg.isNotBlank()
        // We still assert presence to catch accidental removal; if not present, emit a helpful message.
        assertTrue("Expected 'package' attribute on <manifest>, but it was missing or blank in ${manifestFile.path}", hasPackage)
    }

    @Test
    fun application_tag_present() {
        val doc = parseManifest()
        val apps = doc.getElementsByTagName("application")
        assertTrue("No <application> tag present in AndroidManifest.xml at ${manifestFile.path}", apps.length >= 1)
        val app = apps.item(0) as Element
        // Basic sanity: application should be non-empty element
        assertNotNull(app)
    }

    @Test
    fun no_duplicate_application_tags() {
        val doc = parseManifest()
        val apps = doc.getElementsByTagName("application")
        assertTrue("There should be exactly one <application> tag (found ${apps.length})", apps.length == 1)
    }

    @Test
    fun activities_have_valid_names_when_present() {
        val doc = parseManifest()
        val activities = doc.getElementsByTagName("activity")
        for (i in 0 until activities.length) {
            val a = activities.item(i) as Element
            val name = a.getAttribute("android:name")
            assertTrue("Activity #${i} missing android:name", name.isNotBlank())
        }
    }

    @Test
    fun services_receivers_providers_have_names_when_present() {
        val doc = parseManifest()
        val tags = listOf("service", "receiver", "provider")
        for (t in tags) {
            val nodes = doc.getElementsByTagName(t)
            for (i in 0 until nodes.length) {
                val e = nodes.item(i) as Element
                val name = e.getAttribute("android:name")
                assertTrue("<${t}> #${i} missing android:name", name.isNotBlank())
            }
        }
    }

    @Test
    fun queries_or_uses_permissions_are_non_empty_when_present() {
        val doc = parseManifest()
        val usesPerms = doc.getElementsByTagName("uses-permission")
        for (i in 0 until usesPerms.length) {
            val p = usesPerms.item(i) as Element
            val name = p.getAttribute("android:name")
            assertTrue("uses-permission #${i} missing android:name", name.isNotBlank())
        }
        val queries = doc.getElementsByTagName("queries")
        for (i in 0 until queries.length) {
            val q = queries.item(i) as Element
            assertNotNull(q)
        }
    }

    @Test
    fun manifest_rejects_external_entities_for_security() {
        // Validate parser hardening by attempting to parse a crafted string (not writing to repo).
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <!DOCTYPE foo [ <!ENTITY xxe SYSTEM "file:///etc/passwd"> ]>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android" package="test">
                <application android:label="Test"/>
            </manifest>
        """.trimIndent()
        val dbf = DocumentBuilderFactory.newInstance()
        dbf.isExpandEntityReferences = false
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false)
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        val db = dbf.newDocumentBuilder()
        // Parsing should throw due to disallowed DOCTYPE
        assertThrows(Exception::class.java) {
            val bytes = xml.toByteArray()
            db.parse(java.io.ByteArrayInputStream(bytes))
        }
    }
}