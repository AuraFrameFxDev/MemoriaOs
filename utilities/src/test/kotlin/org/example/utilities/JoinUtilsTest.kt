package org.example.utilities

import org.example.list.LinkedList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests for JoinUtils.join which concatenates LinkedList elements separated by a single space.
 *
 * Framework: JUnit 5 (Jupiter)
 */
class JoinUtilsTest {

    private fun linkedListOf(vararg items: String): LinkedList {
        val list = LinkedList()
        for (item in items) {
            // Assuming LinkedList has an add method from Gradle init sample.
            list.add(item)
        }
        return list
    }

    @Nested
    @DisplayName("Happy paths")
    inner class HappyPaths {
        @Test
        fun `empty list returns empty string`() {
            val list = LinkedList()
            val result = JoinUtils.join(list)
            assertEquals("", result)
        }

        @Test
        fun `single element list returns the element without extra spaces`() {
            val list = linkedListOf("hello")
            val result = JoinUtils.join(list)
            assertEquals("hello", result)
        }

        @Test
        fun `two elements result in single space between them`() {
            val list = linkedListOf("hello", "world")
            val result = JoinUtils.join(list)
            assertEquals("hello world", result)
        }

        @Test
        fun `multiple elements join with single spaces only`() {
            val list = linkedListOf("a", "b", "c", "d")
            val result = JoinUtils.join(list)
            assertEquals("a b c d", result)
        }
    }

    @Nested
    @DisplayName("Edge cases")
    inner class EdgeCases {
        @Test
        fun `elements that are empty strings are preserved, yielding consecutive spaces`() {
            val list = linkedListOf("a", "", "b", "")
            val result = JoinUtils.join(list)
            // "a" + " " + "" + " " + "b" + " " + "" -> "a  b "
            assertEquals("a  b ", result)
        }

        @Test
        fun `elements that contain spaces are preserved verbatim`() {
            val list = linkedListOf("hello", "big world", "!")
            val result = JoinUtils.join(list)
            assertEquals("hello big world !", result)
        }

        @Test
        fun `non-ascii characters are handled correctly`() {
            val list = linkedListOf("こんにちは", "世界", "👋")
            val result = JoinUtils.join(list)
            assertEquals("こんにちは 世界 👋", result)
        }

        @Test
        fun `large list joins correctly and does not throw`() {
            val items = (1..1000).map { "v$it" }.toTypedArray()
            val list = linkedListOf(*items)
            val result = assertDoesNotThrow { JoinUtils.join(list) }
            // Validate start/middle/end to avoid building expected full string manually
            val parts = result.split(" ")
            assertEquals(1000, parts.size)
            assertEquals("v1", parts.first())
            assertEquals("v500", parts[499])
            assertEquals("v1000", parts.last())
        }
    }

    @Nested
    @DisplayName("Defensive behavior")
    inner class DefensiveBehavior {
        @Test
        fun `does not prepend or append extra spaces`() {
            val list = linkedListOf("x", "y", "z")
            val result = JoinUtils.join(list)
            // Ensure no leading or trailing whitespace
            assertEquals(result, result.trim())
            assertEquals("x y z", result)
        }
    }
}