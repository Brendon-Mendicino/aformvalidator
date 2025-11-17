package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.ValidationError.NotBlankErr
import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond

public class NotBlankValidator(
    override val annotation: NotBlank
) : ValidatorCond<CharSequence?, NotBlank, NotBlankErr>(annotation) {
    override fun isValid(value: CharSequence?): NotBlankErr? {
        if (value == null) return null

        return if (value.isNotBlank()) null
        else NotBlankErr(annotation)
    }
}