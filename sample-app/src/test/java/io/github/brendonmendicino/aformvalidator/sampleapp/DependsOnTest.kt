package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.DependsOn
import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.Min
import io.github.brendonmendicino.aformvalidator.annotation.ToNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

@FormState
data class FormDepends(
    /** Tutte a me capitano... */
    @ToNumber(Int::class) val numStr: String = ""
) {
    @Min(0)
    @DependsOn(["numStr"])
    val num: Int? = numStr.toIntOrNull()
}

@FormState
data class EmptyDepends(
    /** Tutte a me capitano... */
    @ToNumber(Int::class) val numStr: String = ""
) {
    @Min(0)
    @DependsOn
    val num: Int? = numStr.toIntOrNull()
}

class DependsOnTest {
    @Test
    fun dependsOn_tracks_error_on_derived_field() {
        var form = FormDepends(numStr = "-5").toValidator()
        assertNotNull("Derived num below min → error", form.num.error)

        form = form.copy(numStr = form.numStr.update("12"))
        assertNull("Derived num >= 0 → ok", form.num.error)
    }

    @Test
    fun `testing empty dependency`() {
        var form = EmptyDepends(numStr = "-5").toValidator()
        assertNotNull("Derived num below min → error", form.num.error)

        form = form.update { numStr = "12" }
        assertNull("Derived num >= 0 → ok", form.num.error)
        assertFalse(form.num.used)
        assertEquals(form.num.value, 12)
    }
}

