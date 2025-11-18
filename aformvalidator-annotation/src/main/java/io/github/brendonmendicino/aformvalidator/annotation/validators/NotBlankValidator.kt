package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.NotBlankErr
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

public class NotBlankValidator(
    override val annotation: NotBlank
) : ValidatorCond<CharSequence?, NotBlank, NotBlankErr>(annotation) {
    override fun isValid(value: CharSequence?): NotBlankErr? {
        if (value == null) return null

        return if (value.isNotBlank()) null
        else NotBlankErr(annotation)
    }
}