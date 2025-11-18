package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.annotations.ToNumber
import io.github.brendonmendicino.aformvalidator.core.FormState
import io.github.brendonmendicino.aformvalidator.core.Metadata
import org.junit.Assert.assertEquals
import org.junit.Test

class MetadataTest {
    data class SingleClass(val t: String = "test") : Metadata
    data object ObjectClass : Metadata

    @FormState
    class WithMetadata(
        @ToNumber(metadata = SingleClass::class, numberClass = Int::class)
        val num: String = "two"
    )

    @FormState
    class WithObjectMetadata(
        @ToNumber(metadata = ObjectClass::class, numberClass = Int::class)
        val num: String = "two"
    )

    @Test
    fun `test single class`() {
        val t = WithMetadata().toValidator()

        assertEquals(t.error?.metadata, SingleClass())
    }

    @Test
    fun `test object class`() {
        val t = WithObjectMetadata().toValidator()

        assertEquals(t.error?.metadata, ObjectClass)
    }
}