package io.github.brendonmendicino.aformvalidator.sampleapp

import androidx.annotation.StringRes
import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.Size
import io.github.brendonmendicino.aformvalidator.annotation.ValidationError

@FormState
data class UserFormState(
    @Size(min = 5)
    val list: List<Int> = emptyList(),
    @NotBlank
    val name: String? = "",
    @NotBlank
    val surname: String? = "",
)

@StringRes
fun ValidationError.asRes(): Int = when (this) {
    is ValidationError.NotBlank -> R.string.the_current_field_should_not_be_empty
    else -> throw Exception()
}