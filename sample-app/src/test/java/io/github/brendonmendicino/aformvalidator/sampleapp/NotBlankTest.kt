package io.github.brendonmendicino.aformvalidator.sampleapp

import org.junit.Assert.*
import org.junit.Test
import io.github.brendonmendicino.aformvalidator.annotation.*

@FormState
data class PersonNotBlank(@NotBlank val name: String? = null)

class NotBlankTest {
    @Test
    fun notBlank_behaves_like_kdoc() {
        var person = PersonNotBlank().toValidator()
        assertNotNull("Empty by default should be error", person.name.error)

        person = person.copy(name = person.name.update(" "))
        assertNotNull("Whitespace-only should be error", person.name.error)

        person = person.copy(name = person.name.update("pippo"))
        assertNull("Non-blank should be ok", person.name.error)
    }
}

