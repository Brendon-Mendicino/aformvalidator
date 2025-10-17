package io.github.brendonmendicino.aformvalidator.sampleapp

import org.junit.Assert.*
import org.junit.Test
import io.github.brendonmendicino.aformvalidator.annotation.*

@FormState
data class PersonToNumber(
    val name: String = "",
    @ToNumber(Int::class) val age: String = ""
)

class ToNumberTest {
    @Test
    fun toNumber_kdoc_examples_uses_error() {
        val first = PersonToNumber("First", "1").toValidator()
        assertNull("Parsable Int → ok", first.age.error)

        val empty = PersonToNumber("Anyone", "").toValidator()
        assertNull("Empty string passes according to ToNumber", empty.age.error)

        val second = PersonToNumber("Second", "pluto").toValidator()
        assertNotNull("Non-numeric → error", second.age.error)
    }
}

