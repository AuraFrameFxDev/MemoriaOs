package org.example.list

/**
 * Minimal test-scope contract to satisfy JoinUtils.join expectations.
 * Production code may provide a richer implementation; this is only for tests.
 */
interface LinkedList {
    /**
 * Returns the number of elements in this list.
 *
 * @return the number of elements contained in the list.
 */
fun size(): Int
    /**
 * Returns the element at the specified index, or `null` if no element is present.
 *
 * @param index The zero-based position of the element to retrieve.
 * @return The element at `index`, or `null` if absent.
 */
fun get(index: Int): Any?
}