package io.github.brendonmendicino.aformvalidator.sampleapp

import androidx.annotation.StringRes
import io.github.brendonmendicino.aformvalidator.annotation.Email
import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.Min
import io.github.brendonmendicino.aformvalidator.annotation.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.Pattern
import io.github.brendonmendicino.aformvalidator.annotation.Size
import io.github.brendonmendicino.aformvalidator.annotation.ValidationError

@FormState
data class UserFormState(
    @NotBlank
    val name: String? = "",
    val surname: String? = "",
    @Email
    val email: String? = "",
    @Pattern("hello")
    val test: String? = "",
    @Size(min = 1)
    val list: List<Int> = emptyList(),
    @Min(7)
    val randomNumber: Int = 42,
)

@StringRes
fun ValidationError.asRes(): Int = when (this) {
    is ValidationError.NotBlank -> R.string.the_current_field_should_not_be_empty
    else -> R.string.app_name
}