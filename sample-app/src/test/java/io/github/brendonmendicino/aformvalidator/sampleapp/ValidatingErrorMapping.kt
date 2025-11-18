package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Max
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Min
import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Size
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError
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
        assertTrue(v.s.error is ValidationError.NotBlankErr)
        assertTrue(v.a.error is ValidationError.MinErr)
        assertTrue(v.b.error is ValidationError.MaxErr)
        assertTrue(v.c.error is ValidationError.SizeErr)
    }
}

