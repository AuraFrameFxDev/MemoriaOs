// GradleVersionCatalogTest.kt
// Testing library/framework: kotlin.test (assertions) running on the JUnit 5 platform (assumed typical Kotlin/Gradle setup).
// No new dependencies introduced; tests rely on standard Kotlin/JVM and java.nio APIs.

package testplaceholder

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.fail
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.stream.Stream

class GradleVersionCatalogTest {

    // --------- Data models ----------
    private data class VersionCatalog(
        val versions: Map<String, String>,
        val libraries: Map<String, Library>,
        val plugins: Map<String, Plugin>,
        val bundles: Map<String, List<String>>
    )

    private data class Library(
        val alias: String,
        val group: String,
        val name: String,
        val versionString: String?,
        val versionRef: String?
    )

    private data class Plugin(
        val alias: String,
        val id: String,
        val versionString: String?,
        val versionRef: String?
    )

    // --------- Tests ----------

    @Test
    fun versionCatalogFileShouldExist() {
        val p = locateCatalogPath()
        assertTrue(Files.exists(p), "Expected libs.versions.toml to exist at: $p")
        assertTrue(Files.size(p) > 0, "libs.versions.toml is empty at: $p")
    }

    @Test
    fun versionsShouldNotUseDynamicOrWildcardNotations() {
        val catalog = parseCatalogFromPath(locateCatalogPath())
        val dynamic = catalog.versions.filter { (_, v) ->
            hasDynamicVersion(v)
        }
        assertTrue(
            dynamic.isEmpty(),
            "Dynamic or wildcard versions detected in [versions]: ${dynamic.entries.joinToString { "${it.key}='${it.value}'" }}"
        )
    }

    @Test
    fun librariesAndPluginsReferOnlyToExistingVersionKeys() {
        val c = parseCatalogFromPath(locateCatalogPath())
        val refs = buildList {
            c.libraries.values.mapNotNullTo(this) { it.versionRef }
            c.plugins.values.mapNotNullTo(this) { it.versionRef }
        }.distinct()

        val missing = refs.filter { it !in c.versions.keys }.sorted()
        assertTrue(missing.isEmpty(), "Missing [versions] keys referenced by libraries/plugins: $missing")
    }

    @Test
    fun pluginEntriesHaveIdAndAVersionAttribute() {
        val c = parseCatalogFromPath(locateCatalogPath())
        val missingId = c.plugins.values.filter { it.id.isBlank() }.map { it.alias }
        assertTrue(missingId.isEmpty(), "Plugins missing 'id': $missingId")

        val missingVersion = c.plugins.values.filter { it.versionRef == null && it.versionString == null }.map { it.alias }
        assertTrue(
            missingVersion.isEmpty(),
            "Plugins missing version (either 'version' or 'version.ref' must be set): $missingVersion"
        )
    }

    @Test
    fun libraryEntriesHaveCoordinatesAndSomeVersionAttribute() {
        val c = parseCatalogFromPath(locateCatalogPath())
        val missingCoords = c.libraries.values.filter { it.group.isBlank() || it.name.isBlank() }.map { it.alias }
        assertTrue(missingCoords.isEmpty(), "Libraries missing 'group' and/or 'name' (or 'module'): $missingCoords")

        val missingVersion = c.libraries.values.filter { it.versionRef == null && it.versionString == null }.map { it.alias }
        assertTrue(
            missingVersion.isEmpty(),
            "Libraries missing version (either 'version' or 'version.ref' must be set): $missingVersion"
        )
    }

    @Test
    fun libraryArtifactCoordinatesMustBeUniqueAcrossAliases() {
        val c = parseCatalogFromPath(locateCatalogPath())
        val seen = mutableMapOf<String, String>()
        val dups = mutableListOf<String>()
        for (lib in c.libraries.values) {
            val coord = "${lib.group}:${lib.name}"
            val prev = seen.putIfAbsent(coord, lib.alias)
            if (prev != null && prev != lib.alias) {
                dups += "$coord duplicated by aliases '$prev' and '${lib.alias}'"
            }
        }
        assertTrue(dups.isEmpty(), "Duplicate library coordinates found: ${dups.joinToString("; ")}")
    }

    @Test
    fun aliasesUseSafeCharacters() {
        val c = parseCatalogFromPath(locateCatalogPath())
        val aliasPattern = Regex("^[A-Za-z0-9_.-]+$")
        val allAliases = c.libraries.keys + c.plugins.keys + c.bundles.keys
        val bad = allAliases.filterNot { aliasPattern.matches(it) }
        assertTrue(bad.isEmpty(), "Aliases contain unsupported characters: $bad")
    }

    @Test
    fun bundlesOnlyReferenceExistingLibraryAliases() {
        val c = parseCatalogFromPath(locateCatalogPath())
        val libAliases = c.libraries.keys.toSet()
        val invalid = mutableListOf<String>()
        for ((bundle, refs) in c.bundles) {
            val missing = refs.filter { it !in libAliases }
            if (missing.isNotEmpty()) invalid += "$bundle -> missing $missing"
        }
        assertTrue(invalid.isEmpty(), "Bundles referencing non-existent library aliases: ${invalid.joinToString("; ")}")
    }

    @Test
    fun kotlinVersionAlignmentIfPresent() {
        val c = parseCatalogFromPath(locateCatalogPath())

        val kotlinVer = c.versions["kotlin"]
        val kotlinPlugins = c.plugins.values.filter { it.id.lowercase(Locale.ROOT).startsWith("org.jetbrains.kotlin") }

        // If Kotlin plugin is present, ensure it aligns to versions.kotlin (via version.ref or explicit version == versions.kotlin)
        val mismatchedPlugins = kotlinPlugins.filter { p ->
            when {
                kotlinVer == null -> false // nothing to align against
                p.versionRef == "kotlin" -> false
                p.versionString != null && p.versionString == kotlinVer -> false
                else -> true
            }
        }.map { it.alias }

        assertTrue(
            mismatchedPlugins.isEmpty(),
            "Kotlin plugin versions should align with [versions].kotlin via 'version.ref = \"kotlin\"' or same explicit string. Mismatches: $mismatchedPlugins"
        )

        // If Kotlin libraries exist, ensure they also align to Kotlin version key when available.
        if (kotlinVer != null) {
            val kotlinLibs = c.libraries.values.filter { it.group == "org.jetbrains.kotlin" }
            val mismatchedLibs = kotlinLibs.filter { lib ->
                when {
                    lib.versionRef == "kotlin" -> false
                    lib.versionString != null && lib.versionString == kotlinVer -> false
                    else -> true
                }
            }.map { it.alias }
            assertTrue(
                mismatchedLibs.isEmpty(),
                "Kotlin libraries should align with [versions].kotlin via 'version.ref = \"kotlin\"' or same explicit string. Mismatches: $mismatchedLibs"
            )
        }
    }

    @Test
    fun noDynamicOrWildcardPluginVersions() {
        val c = parseCatalogFromPath(locateCatalogPath())
        val offenders = c.plugins.values.filter {
            val v = it.versionString
            v != null && hasDynamicVersion(v)
        }.map { it.alias }
        assertTrue(offenders.isEmpty(), "Dynamic/wildcard plugin versions detected: $offenders")
    }

    // --------- Helpers & Parser ----------

    private fun hasDynamicVersion(v: String): Boolean {
        // Reject '+' wildcards and Gradle dynamic keywords. Allow '-SNAPSHOT' as some projects may use it intentionally.
        val plusWildcard = v.contains('+')
        val dynamicKeyword = v.lowercase(Locale.ROOT).contains("latest.release") ||
                v.lowercase(Locale.ROOT).contains("latest.integration")
        return plusWildcard || dynamicKeyword
    }

    private fun locateCatalogPath(): Path {
        val default = Paths.get("gradle", "libs.versions.toml")
        if (Files.exists(default)) return default

        // Fallback: find libs.versions.toml within depth to avoid heavy traversal
        Files.find(Paths.get("."), 6) { p, _ -> p.fileName?.toString() == "libs.versions.toml" }.use { stream ->
            val found = stream.findFirst()
            if (found.isPresent) return found.get().toAbsolutePath().normalize()
        }
        fail("Could not locate libs.versions.toml (looked in gradle/libs.versions.toml and project tree).")
        throw IllegalStateException("Unreachable")
    }

    private fun parseCatalogFromPath(path: Path): VersionCatalog {
        val content = Files.readString(path, StandardCharsets.UTF_8)
        return parseCatalog(content)
    }

    private fun parseCatalog(content: String): VersionCatalog {
        val logical = toLogicalLines(content)
        val versions = mutableMapOf<String, String>()
        val libraries = mutableMapOf<String, Library>()
        val plugins = mutableMapOf<String, Plugin>()
        val bundles = mutableMapOf<String, List<String>>()

        var section: String? = null
        for (raw in logical) {
            val line = raw.trim()
            if (line.isEmpty()) continue

            if (line.startsWith("[") && line.endsWith("]")) {
                section = line.substring(1, line.length - 1).trim()
                continue
            }

            when (section) {
                "versions" -> parseVersionEntry(line, versions)
                "libraries" -> parseLibraryEntry(line, libraries)
                "plugins" -> parsePluginEntry(line, plugins)
                "bundles" -> parseBundleEntry(line, bundles)
                else -> {
                    // ignore unknown sections
                }
            }
        }
        return VersionCatalog(versions, libraries, plugins, bundles)
    }

    private fun parseVersionEntry(line: String, out: MutableMap<String, String>) {
        val m = VERSION_KV.find(line) ?: return
        val key = m.groupValues[1]
        val value = m.groupValues[2]
        out[key] = value
    }

    private fun parseLibraryEntry(line: String, out: MutableMap<String, Library>) {
        val m = ALIAS_OBJ.find(line) ?: return
        val alias = m.groupValues[1]
        val body = m.groupValues[2]

        val module = MODULE_KV.find(body)?.groupValues?.get(1)
        val group = GROUP_KV.find(body)?.groupValues?.get(1)
        val name = NAME_KV.find(body)?.groupValues?.get(1)
        val vRef = VERSION_REF_KV.find(body)?.groupValues?.get(1)
        val vStr = VERSION_KV.find(body)?.groupValues?.get(1)

        val (g, n) = if (module != null) {
            val parts = module.split(":")
            if (parts.size == 2) parts[0] to parts[1] else "" to ""
        } else {
            (group ?: "") to (name ?: "")
        }

        out[alias] = Library(
            alias = alias,
            group = g,
            name = n,
            versionString = vStr,
            versionRef = vRef
        )
    }

    private fun parsePluginEntry(line: String, out: MutableMap<String, Plugin>) {
        val m = ALIAS_OBJ.find(line) ?: return
        val alias = m.groupValues[1]
        val body = m.groupValues[2]

        val id = ID_KV.find(body)?.groupValues?.get(1) ?: ""
        val vRef = VERSION_REF_KV.find(body)?.groupValues?.get(1)
        val vStr = VERSION_KV.find(body)?.groupValues?.get(1)

        out[alias] = Plugin(
            alias = alias,
            id = id,
            versionString = vStr,
            versionRef = vRef
        )
    }

    private fun parseBundleEntry(line: String, out: MutableMap<String, List<String>>) {
        val m = ALIAS_ARRAY.find(line) ?: return
        val alias = m.groupValues[1]
        val items = ARRAY_ITEMS.findAll(m.groupValues[2])
            .map { it.groupValues[1] }
            .toList()
        out[alias] = items
    }

    private fun toLogicalLines(content: String): List<String> {
        // Combine multi-line TOML objects/arrays into single logical lines for simple regex parsing
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var braceDepth = 0
        var bracketDepth = 0
        content.lineSequence().forEach { raw ->
            val line = stripLineComments(raw).trim()
            if (line.isEmpty()) return@forEach

            // Section headers commit immediately
            if (line.startsWith("[") && line.endsWith("]") && braceDepth == 0 && bracketDepth == 0) {
                if (sb.isNotEmpty()) {
                    result += sb.toString().trim()
                    sb.setLength(0)
                }
                result += line
                return@forEach
            }

            sb.append(if (sb.isEmpty()) line else " $line")

            braceDepth += countChar(line, '{') - countChar(line, '}')
            bracketDepth += countChar(line, '[') - countChar(line, ']')

            if (braceDepth == 0 && bracketDepth == 0) {
                result += sb.toString().trim()
                sb.setLength(0)
            }
        }
        if (sb.isNotEmpty()) {
            result += sb.toString().trim()
        }
        return result
    }

    private fun stripLineComments(s: String): String {
        // Remove comments starting with '#' when not inside quotes
        var inQuotes = false
        val out = StringBuilder()
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '"') {
                inQuotes = !inQuotes
                out.append(c)
            } else if (c == '#' && !inQuotes) {
                break
            } else {
                out.append(c)
            }
            i++
        }
        return out.toString()
    }

    private fun countChar(s: String, ch: Char): Int {
        var n = 0
        for (c in s) if (c == ch) n++
        return n
    }

    // --------- Regexes ----------
    private val VERSION_KV = Regex("""^\s*([A-Za-z0-9_.-]+)\s*=\s*"([^"]+)"\s*$""")
    private val ALIAS_OBJ = Regex("""^\s*([A-Za-z0-9_.-]+)\s*=\s*\{(.*)}\s*$""")
    private val ALIAS_ARRAY = Regex("""^\s*([A-Za-z0-9_.-]+)\s*=\s*\[(.*)]\s*$""")
    private val ARRAY_ITEMS = Regex(""""([^"]+)"""")
    private val GROUP_KV = Regex("""group\s*=\s*"([^"]+)"""")
    private val NAME_KV = Regex("""name\s*=\s*"([^"]+)"""")
    private val MODULE_KV = Regex("""module\s*=\s*"([^"]+)"""")
    private val VERSION_REF_KV = Regex("""version\.ref\s*=\s*"([^"]+)"""")
    private val ID_KV = Regex("""id\s*=\s*"([^"]+)"""")

    // --------- Utility ----------
    private fun <T> buildList(builderAction: MutableList<T>.() -> Unit): List<T> {
        val list = mutableListOf<T>()
        list.builderAction()
        return list
    }
}