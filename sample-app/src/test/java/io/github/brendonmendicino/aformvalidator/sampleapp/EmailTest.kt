package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.annotations.Email
import io.github.brendonmendicino.aformvalidator.core.FormState
import org.junit.Assert.assertNull
import org.junit.Test

class EmailTest {
    @FormState
    data class PersonEmail(
        val name: String = "",
        @Email val personal: String = "",
        @Email(pattern = """\w+@mydomain\.com""") val work: String = "",
    )

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
