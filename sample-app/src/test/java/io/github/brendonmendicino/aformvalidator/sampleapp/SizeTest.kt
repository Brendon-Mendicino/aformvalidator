package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Size
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SizeTest {
    @FormState
    data class BoundList(@Size(2, 10) val list: List<Int>)

    @FormState
    data class BoundString(@Size(2, 10) val str: String)

    @FormState
    data class BoundMap(@Size(2, 10) val map: Map<Any, Any>)

    @FormState
    data class SomethingElse(@Size(2, 10) val num: Long)

    @FormState
    data class SizeNotBlank(
        @NotBlank
        @Size(max = 10)
        val str: String?,
    )

    @Test
    fun size_kdoc_examples_uses_error() {
        val empty = BoundList(listOf()).toValidator()
        assertNotNull("size < min → error", empty.list.error)

        val withElements = BoundList(listOf(1, 2, 3)).toValidator()
        assertNull("within [2,10] → ok", withElements.list.error)
    }

    @Test
    fun string_size_is_between_the_range() {
        val state = BoundString("hello").toValidator()
        assertNull(state.str.error)
    }

    @Test
    fun string_size_is_outside_the_range() {
        val state1 = BoundString("").toValidator()
        val state2 = BoundString("hello to this damned place").toValidator()

        assertNotNull(state1.str.error)
        assertNotNull(state2.str.error)
    }


    @Test
    fun map_size_is_between_the_range() {
        val state = BoundMap(mapOf(1 to 1, 2 to 2, 3 to 3)).toValidator()
        assertNull(state.map.error)
    }

    @Test
    fun map_size_is_outside_the_range() {
        val state1 = BoundMap(mapOf()).toValidator()
        val state2 = BoundMap((0..20).associateWith { it }).toValidator()

        assertNotNull(state1.map.error)
        assertNotNull(state2.map.error)
    }

    @Test
    fun something_without_a_size_should_be_valid() {
        val s = SomethingElse(1).toValidator()
        assertNull(s.num.error)
    }

    @Test
    fun string_with_not_blank_should_not_error() {
        val s = SizeNotBlank("hello").toValidator()
        assertNull(s.str.error)
    }

    @Test
    fun string_with_not_blank_full_bank_is_error() {
        val s = SizeNotBlank("    ").toValidator()
        assertEquals(s.str.error?.run { this::class }, ValidationError.NotBlankErr::class)
    }
}

