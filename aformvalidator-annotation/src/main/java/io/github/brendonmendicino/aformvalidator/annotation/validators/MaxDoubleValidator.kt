package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.annotations.MaxDouble
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.MaxDoubleErr
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

public class MaxDoubleValidator(
    override val metadata: Metadata?,
    override val annotation: MaxDouble
) : ValidatorCond<Number?, MaxDouble, MaxDoubleErr>(metadata, annotation) {
    override fun isValid(value: Number?): MaxDoubleErr? {
        if (value == null) return null

        return if (value.toDouble() > annotation.max) MaxDoubleErr(metadata, annotation)
        else null
    }
}
