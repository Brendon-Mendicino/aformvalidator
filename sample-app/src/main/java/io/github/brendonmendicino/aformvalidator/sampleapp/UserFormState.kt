package io.github.brendonmendicino.aformvalidator.sampleapp

import androidx.annotation.StringRes
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Email
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Min
import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Pattern
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Size
import io.github.brendonmendicino.aformvalidator.annotation.annotations.ToNumber
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError
import io.github.brendonmendicino.aformvalidator.core.DependsOn
import io.github.brendonmendicino.aformvalidator.core.FormState

@FormState
data class UserFormState(
    @NotBlank
    val name: String? = "",
    val surname: String? = "",
    @Email
    val email: String? = "",
    @NotBlank
    @Pattern(regex = "hello")
    val test: String? = "",
    @Size(min = 1)
    val list: List<Int> = emptyList(),
    @Min(min = 7)
    val randomNumber: Int = 42,
    @ToNumber(numberClass = Int::class)
    val num: String = ""
) {
    @Min(min = 3)
    @DependsOn(["num"])
    val derived: Int? = num.toIntOrNull()
}

@StringRes
fun ValidationError<*>.asRes(): Int = when (this) {
    is ValidationError.NotBlankErr -> R.string.the_current_field_should_not_be_empty
    else -> R.string.app_name
}