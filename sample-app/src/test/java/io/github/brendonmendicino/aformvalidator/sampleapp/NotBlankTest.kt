package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotBlank
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class NotBlankTest {
    @FormState
    data class PersonNotBlank(@NotBlank val name: String? = null)

    @Test
    fun notBlank_behaves_like_kdoc() {
        var person = PersonNotBlank().toValidator()
        assertNull("Null string should not be error", person.name.error)

        person = person.copy(name = person.name.update(" "))
        assertNotNull("Whitespace-only should be error", person.name.error)

        person = person.update { name = "" }
        assertNotNull("Empty string should be error", person.name.error)

        person = person.copy(name = person.name.update("pippo"))
        assertNull("Non-blank should be ok", person.name.error)
    }
}

