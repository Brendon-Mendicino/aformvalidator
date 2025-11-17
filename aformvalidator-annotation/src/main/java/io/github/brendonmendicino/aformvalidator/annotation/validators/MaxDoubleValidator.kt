package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.MaxDouble
import io.github.brendonmendicino.aformvalidator.annotation.ValidationError.MaxDoubleErr
import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond

public class MaxDoubleValidator(override val annotation: MaxDouble) :
    ValidatorCond<Number?, MaxDouble, MaxDoubleErr>(annotation) {
    override fun isValid(value: Number?): MaxDoubleErr? {
        if (value == null) return null

        return if (value.toDouble() > annotation.max) MaxDoubleErr(annotation)
        else null
    }
}
