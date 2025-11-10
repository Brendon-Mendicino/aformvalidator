package io.github.brendonmendicino.aformvalidator.sampleapp

import org.junit.Test
import org.junit.Assert.*

class UpdaterTest {
    @Test
    fun `test updater function for state`() {
        val v = UserFormState().toValidator()

        assertEquals(v.update { email = "boh" }, v.copy(email = v.email.update("boh")))
        assertEquals(
            v.update { email = "test" }.update { name = "bello" },
            v.copy(email = v.email.update("test"), name = v.name.update("bello"))
        )
    }
}