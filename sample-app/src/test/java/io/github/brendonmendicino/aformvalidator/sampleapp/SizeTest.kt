package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.Size
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

@FormState
data class BoundList(@Size(2, 10) val list: List<Int>)

@FormState
data class BoundString(@Size(2, 10) val str: String)

@FormState
data class BoundMap(@Size(2, 10) val map: Map<Any, Any>)

@FormState
data class SomethingElse(@Size(2, 10) val num: Long)

class SizeTest {
    @Test
    fun size_kdoc_examples_uses_error() {
        val empty = BoundList(listOf()).toValidator()
        assertNotNull("size < min → error", empty.list.error)

        val withElements = BoundList(listOf(1, 2, 3)).toValidator()
        assertNull("within [2,10] → ok", withElements.list.error)
    }

    @Test
    fun `string size is between the range`() {
        val state = BoundString("hello").toValidator()
        assertNull(state.str.error)
    }

    @Test
    fun `string size is outside the range`() {
        val state1 = BoundString("").toValidator()
        val state2 = BoundString("hello to this damned place").toValidator()

        assertNotNull(state1.str.error)
        assertNotNull(state2.str.error)
    }


    @Test
    fun `map size is between the range`() {
        val state = BoundMap(mapOf(1 to 1, 2 to 2, 3 to 3)).toValidator()
        assertNull(state.map.error)
    }

    @Test
    fun `map size is outside the range`() {
        val state1 = BoundMap(mapOf()).toValidator()
        val state2 = BoundMap((0..20).associateWith { it }).toValidator()

        assertNotNull(state1.map.error)
        assertNotNull(state2.map.error)
    }

    @Test
    fun `something without a size should be valid`() {
        val s = SomethingElse(1).toValidator()
        assertNull(s.num.error)
    }
}

