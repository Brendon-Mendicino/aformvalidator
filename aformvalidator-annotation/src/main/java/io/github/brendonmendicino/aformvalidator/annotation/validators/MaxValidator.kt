package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.Max
import io.github.brendonmendicino.aformvalidator.annotation.ValidationError.MaxErr
import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond

public class MaxValidator(override val annotation: Max) :
    ValidatorCond<Number?, Max, MaxErr>(annotation) {
    override fun isValid(value: Number?): MaxErr? {
        if (value == null) return null

        return if (value.toLong() > annotation.max) MaxErr(annotation)
        else null
    }
}