package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond
import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotNull
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.NotNullErr

public class NotNullValidator(
    override val annotation: NotNull
) : ValidatorCond<Any?, NotNull, NotNullErr>(annotation) {
    override fun isValid(value: Any?): NotNullErr? {
        return if (value == null) NotNullErr(annotation)
        else null
    }
}