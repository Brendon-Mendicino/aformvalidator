package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.Max
import io.github.brendonmendicino.aformvalidator.annotation.Min
import io.github.brendonmendicino.aformvalidator.annotation.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.Size
import io.github.brendonmendicino.aformvalidator.annotation.ValidationError
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationErrorMappingTest {
    @FormState
    class M(
        @NotBlank val s: String? = " ",
        @Min(5) val a: Int = 1,
        @Max(10) val b: Int = 99,
        @Size(2, 3) val c: List<Int> = listOf()
    )

    @Test
    fun errors_map_to_sealed_types() {
        val v = M().toValidator()
        assertTrue(v.s.error is ValidationError.NotBlank)
        assertTrue(v.a.error is ValidationError.Min)
        assertTrue(v.b.error is ValidationError.Max)
        assertTrue(v.c.error is ValidationError.Size)
    }
}

