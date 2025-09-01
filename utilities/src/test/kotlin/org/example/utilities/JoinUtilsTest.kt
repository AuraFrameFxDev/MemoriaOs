package org.example.utilities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll

/**
 * Testing library/framework: JUnit Jupiter (JUnit 5).
 *
 * This test suite verifies JoinUtils.join behavior across:
 * - empty list
 * - single element
 * - multiple elements (spacing rules)
 * - elements containing spaces
 * - null-like or unexpected inputs via a defensive fake (size changes mid-iteration)
 * - very large inputs performance-wise (sanity check without heavy cost)
 *
 * We provide a lightweight test-double for org.example.list.LinkedList to avoid external deps.
 */
class JoinUtilsTest {

    // Minimal interface expected by JoinUtils.join
    interface TestLinkedList {
        fun size(): Int
        fun get(index: Int): Any?
    }

    // Adapter to satisfy the expected type at call site using typealias within this test scope.
    // If org.example.list.LinkedList exists in the project, these fakes emulate just what we need.
    private class SimpleLinkedList(private val items: List<Any?>) : TestLinkedList {
        override fun size(): Int = items.size
        override fun get(index: Int): Any? = items[index]
    }

    // Faulty fake to simulate size changing during iteration to ensure join uses size() snapshot/looping correctly
    private class FlakyLinkedList(private val backing: MutableList<Any?>) : TestLinkedList {
        private var calls = 0
        override fun size(): Int {
            calls++
            // After first call, change the reported size to simulate concurrent modification
            return if (calls <= 1) backing.size else maxOf(0, backing.size - 1)
        }
        override fun get(index: Int): Any? = backing[index]
    }

    // Helper to bridge our TestLinkedList to the expected signature type without importing project class:
    // We define a local shim with the same binary shape: size() and get(Int).
    // Kotlin is structural here only at compile level for our local use; to avoid classpath conflicts,
    // we create a local data holder whose methods are forwarded via an anonymous object cast.
    @Suppress("UNCHECKED_CAST")
    private fun toExpected(list: TestLinkedList): org.example.list.LinkedList {
        // Create a dynamic proxy-like shim via anonymous object with matching methods.
        val shim = object {
            fun size(): Int = list.size()
            fun get(i: Int): Any? = list.get(i)
        }
        return shim as org.example.list.LinkedList
    }

    @Nested
    @DisplayName("join: basic cases")
    inner class BasicCases {

        @Test
        fun `returns empty string for empty list`() {
            val src = SimpleLinkedList(emptyList())
            val result = JoinUtils.join(toExpected(src))
            assertEquals("", result)
        }

        @Test
        fun `single element no extra spaces`() {
            val src = SimpleLinkedList(listOf("alpha"))
            val result = JoinUtils.join(toExpected(src))
            assertEquals("alpha", result)
        }

        @Test
        fun `two elements separated by single space`() {
            val src = SimpleLinkedList(listOf("alpha", "beta"))
            val result = JoinUtils.join(toExpected(src))
            assertEquals("alpha beta", result)
        }

        @Test
        fun `multiple elements produce single spaces only between`() {
            val src = SimpleLinkedList(listOf("a","b","c","d"))
            val result = JoinUtils.join(toExpected(src))
            assertEquals("a b c d", result)
            assertFalse(result.contains("  "))
            assertTrue(result.count { it == ' ' } == 3)
        }
    }

    @Nested
    @DisplayName("join: element content edge cases")
    inner class ElementContentCases {

        @Test
        fun `elements containing internal spaces are preserved verbatim`() {
            val src = SimpleLinkedList(listOf("hello world", "kotlin  test", " end "))
            val result = JoinUtils.join(toExpected(src))
            assertEquals("hello world kotlin  test  end ", result)
        }

        @Test
        fun `null elements are converted to string literal null`() {
            val src = SimpleLinkedList(listOf("x", null, "y"))
            val result = JoinUtils.join(toExpected(src))
            // Kotlin's StringBuilder.append(Any?) uses "null" for null values
            assertEquals("x null y", result)
        }

        @Test
        fun `non-string elements are stringified via toString`() {
            data class P(val x:Int, val y:Int)
            val src = SimpleLinkedList(listOf(123, true, P(1,2)))
            val result = JoinUtils.join(toExpected(src))
            assertAll(
                { assertTrue(result.startsWith("123 true P(x=1, y=2)".substring(0, 7))) },
                { assertEquals("123 true P(x=1, y=2)", result) }
            )
        }
    }

    @Nested
    @DisplayName("join: robustness")
    inner class Robustness {

        @Test
        fun `handles size fluctuation after first size check gracefully`() {
            val src = FlakyLinkedList(mutableListOf("a","b","c"))
            val result = JoinUtils.join(toExpected(src))
            // Because loop bounds use '0 until source.size()', the size is evaluated once.
            // First size() => 3, so join should attempt to get indices 0,1,2 which exist.
            assertEquals("a b c", result)
        }

        @Test
        fun `large input remains efficient and correct`() {
            val n = 2000
            val items = (1..n).map { it.toString() }
            val src = SimpleLinkedList(items)
            val result = JoinUtils.join(toExpected(src))
            assertTrue(result.startsWith("1 2 3 4 5"))
            assertTrue(result.endsWith("${n-1} $n"))
            // basic sanity: number of spaces should be n-1
            val spaces = result.count { it == ' ' }
            assertEquals(n - 1, spaces)
        }
    }
}