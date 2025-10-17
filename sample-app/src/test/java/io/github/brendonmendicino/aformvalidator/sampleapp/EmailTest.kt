package io.github.brendonmendicino.aformvalidator.sampleapp

import org.junit.Assert.*
import org.junit.Test
import io.github.brendonmendicino.aformvalidator.annotation.*

@FormState
data class PersonEmail(
    val name: String = "",
    @Email val personal: String = "",
    @Email(pattern = """\w+@mydomain\.com""") val work: String = ""
)

class EmailTest {
    @Test
    fun email_accepts_valid_addresses() {
        val ok = PersonEmail(
            name = "A",
            personal = "user@example.com",
            work = "john@mydomain.com"
        ).toValidator()

        assertNull("Generic valid email should pass", ok.personal.error)
        assertNull("Custom domain should pass", ok.work.error)
    }
}
