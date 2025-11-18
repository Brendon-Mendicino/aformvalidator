package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotNull
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.NotNullErr
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

public class NotNullValidator(
    override val metadata: Metadata?, override val annotation: NotNull
) : ValidatorCond<Any?, NotNull, NotNullErr>(metadata, annotation) {
    override fun isValid(value: Any?): NotNullErr? {
        return if (value == null) NotNullErr(metadata, annotation)
        else null
    }
}