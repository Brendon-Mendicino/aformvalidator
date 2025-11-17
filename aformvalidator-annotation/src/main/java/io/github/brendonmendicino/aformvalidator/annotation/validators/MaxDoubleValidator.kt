package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond
import io.github.brendonmendicino.aformvalidator.annotation.annotations.MaxDouble
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.MaxDoubleErr

public class MaxDoubleValidator(override val annotation: MaxDouble) :
    ValidatorCond<Number?, MaxDouble, MaxDoubleErr>(annotation) {
    override fun isValid(value: Number?): MaxDoubleErr? {
        if (value == null) return null

        return if (value.toDouble() > annotation.max) MaxDoubleErr(annotation)
        else null
    }
}
