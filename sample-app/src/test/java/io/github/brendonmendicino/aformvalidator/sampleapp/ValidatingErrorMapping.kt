package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.*
import org.junit.Assert.*
import org.junit.Test

@FormState
class M(
    @NotBlank val s: String? = " ",
    @Min(5) val a: Int = 1,
    @Max(10) val b: Int = 99,
    @Size(2, 3) val c: List<Int> = listOf()
)

class ValidationErrorMappingTest {
    @Test
    fun errors_map_to_sealed_types() {
        val v = M().toValidator()
        assertTrue(v.s.error is ValidationError.NotBlank)
        assertTrue(v.a.error is ValidationError.Min)
        assertTrue(v.b.error is ValidationError.Max)
        assertTrue(v.c.error is ValidationError.Size)
    }
}

