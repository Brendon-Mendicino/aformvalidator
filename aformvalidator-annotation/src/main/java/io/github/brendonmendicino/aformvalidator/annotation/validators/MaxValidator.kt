package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.annotations.Max
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.MaxErr
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

public class MaxValidator(override val annotation: Max) :
    ValidatorCond<Number?, Max, MaxErr>(annotation) {
    override fun isValid(value: Number?): MaxErr? {
        if (value == null) return null

        return if (value.toLong() > annotation.max) MaxErr(annotation)
        else null
    }
}