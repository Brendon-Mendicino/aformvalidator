package io.github.brendonmendicino.aformvalidator.sampleapp

import org.junit.Assert.*
import org.junit.Test
import io.github.brendonmendicino.aformvalidator.annotation.*

@FormState
data class BoundList(@Size(2, 10) val list: List<Int>)

class SizeTest {
    @Test
    fun size_kdoc_examples_uses_error() {
        val empty = BoundList(listOf()).toValidator()
        assertNotNull("size < min → error", empty.list.error)

        val withElements = BoundList(listOf(1, 2, 3)).toValidator()
        assertNull("within [2,10] → ok", withElements.list.error)
    }
}

