package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.FormState

@FormState
data class UserFormState(
    @NotBlank
    val name: String? = "",
    @NotBlank
    val surname: String? = "",
)
