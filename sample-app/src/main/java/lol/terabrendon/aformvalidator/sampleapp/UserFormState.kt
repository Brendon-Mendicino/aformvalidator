package lol.terabrendon.aformvalidator.sampleapp

import lol.terabrendon.aformvalidator.annotation.FormState

@FormState
data class UserFormState(
    @NotBlank
    val name: String? = "",
    @NotBlank
    val surname: String? = "",
)
