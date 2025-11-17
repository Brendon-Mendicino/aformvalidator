package io.github.brendonmendicino.aformvalidator.sampleapp

import androidx.annotation.StringRes
import io.github.brendonmendicino.aformvalidator.annotation.annotations.DependsOn
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Email
import io.github.brendonmendicino.aformvalidator.annotation.annotations.FormState
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Min
import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Pattern
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Size
import io.github.brendonmendicino.aformvalidator.annotation.annotations.ToNumber
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError

@FormState
data class UserFormState(
    @NotBlank
    val name: String? = "",
    val surname: String? = "",
    @Email
    val email: String? = "",
    @NotBlank
    @Pattern("hello")
    val test: String? = "",
    @Size(min = 1)
    val list: List<Int> = emptyList(),
    @Min(7)
    val randomNumber: Int = 42,
    @ToNumber(Int::class)
    val num: String = ""
) {
    @Min(3)
    @DependsOn(["num"])
    val derived: Int? = num.toIntOrNull()
}

@StringRes
fun ValidationError<*>.asRes(): Int = when (this) {
    is ValidationError.NotBlankErr -> R.string.the_current_field_should_not_be_empty
    else -> R.string.app_name
}