package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.*
import org.junit.Assert.*
import org.junit.Test

@FormState
data class FormDepends(
    @ToNumber(Int::class) val numStr: String = ""
) {
    @Min(0)
    @DependsOn(["numStr"])
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
}

