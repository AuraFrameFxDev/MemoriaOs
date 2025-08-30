/*
  AndroidManifestValidationTest
  Purpose: Validate the structure and critical invariants of src/main/AndroidManifest.xml
  Testing stack: JUnit 5 (Jupiter) — detected from build files at generation time.
  Notes:
    - Tests focus on the PR diff introducing/altering the manifest.
    - We validate well-formedness, namespace correctness, and absence of duplicate permissions.
    - File resolution assumes Gradle test working dir at module root; multiple fallbacks included.
*/

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.w3c.dom.Element

private const val ANDROID_NS_URI = "http://schemas.android.com/apk/res/android"

class AndroidManifestValidationTest {

  private fun manifestPathCandidates(): List<Path> {
    val userDir = Paths.get(System.getProperty("user.dir"))
    val roots = buildList {
      add(userDir)
      add(userDir.resolve("core-module"))
      userDir.parent?.let { add(it.resolve("core-module")) }
    }
    val rels = listOf(
      "src/main/AndroidManifest.xml",
      "AndroidManifest.xml",
      "src/AndroidManifest.xml"
    )
    return roots.flatMap { r -> rels.map { r.resolve(it) } }.distinct()
  }

  private fun resolveManifestPath(): Path =
    manifestPathCandidates().firstOrNull { Files.exists(it) }
      ?: throw IllegalStateException("AndroidManifest.xml not found. Checked: ${manifestPathCandidates().joinToString()}")

  private fun readManifestText(path: Path): String =
    Files.newBufferedReader(path, StandardCharsets.UTF_8).use { it.readText() }

  private fun parse(path: Path) = DocumentBuilderFactory.newInstance().apply {
    isNamespaceAware = true
    // Harden parser: prevent XXE/DTD-related attacks
    setFeature("http://xml.org/sax/features/external-general-entities", false)
    setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "")
    setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "")
  }.newDocumentBuilder().parse(path.toFile())

  @Test
  @DisplayName("Manifest file exists at a known location")
  fun manifestFileExists() {
    val exists = manifestPathCandidates().any { Files.exists(it) }
    assertTrue(exists, "AndroidManifest.xml missing. Checked: ${manifestPathCandidates().joinToString()}")
  }

  @Test
  @DisplayName("Manifest is well-formed XML and root element is <manifest>")
  fun manifestIsWellFormedAndHasRootManifest() {
    val path = resolveManifestPath()
    assertDoesNotThrow({ parse(path) }, "Manifest should parse without XML errors")
    val doc = parse(path)
    val root = doc.documentElement
    assertNotNull(root, "Document should have a root element")
    assertEquals("manifest", root.tagName, "Root element must be <manifest>")
  }

  @Test
  @DisplayName("Android XML namespace is declared and correct")
  fun hasAndroidNamespaceWithCorrectUri() {
    val path = resolveManifestPath()
    val doc = parse(path)
    val root = doc.documentElement
    val declared = root.getAttribute("xmlns:android")
    assertEquals(ANDROID_NS_URI, declared, "xmlns:android must be $ANDROID_NS_URI")
  }

  @Test
  @DisplayName("No duplicate <uses-permission> declarations")
  fun hasNoDuplicateUsesPermissionEntries() {
    val path = resolveManifestPath()
    val doc = parse(path)
    val nodes = doc.getElementsByTagName("uses-permission")
    val names = mutableListOf<String>()
    for (i in 0 until nodes.length) {
      val el = nodes.item(i) as Element
      val name = el.getAttributeNS(ANDROID_NS_URI, "name").ifEmpty { el.getAttribute("android:name") }
      if (name.isNotBlank()) names += name
    }
    val duplicates = names.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
    assertTrue(duplicates.isEmpty(), "Duplicate permissions found: $duplicates")
  }

  @Test
  @DisplayName("Manifest does not declare a DOCTYPE (kept simple and secure)")
  fun containsNoDoctypeDeclaration() {
    val path = resolveManifestPath()
    val text = readManifestText(path)
    assertFalse(text.contains("<!DOCTYPE", ignoreCase = true), "Manifest should not declare a DOCTYPE")
  }
}