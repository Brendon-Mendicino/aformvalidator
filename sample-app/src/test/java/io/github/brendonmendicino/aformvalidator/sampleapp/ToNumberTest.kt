package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.annotations.ToNumber
import io.github.brendonmendicino.aformvalidator.core.FormState
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ToNumberTest {
    @FormState
    data class PersonToNumber(
        val name: String = "",
        @ToNumber(numberClass = Int::class)
        val age: String = ""
    )

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

