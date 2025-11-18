package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.annotations.Min
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.MinErr
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

public class MinValidator(override val annotation: Min) :
    ValidatorCond<Number?, Min, MinErr>(annotation) {
    override fun isValid(value: Number?): MinErr? {
        if (value == null) return null

        return if (value.toLong() < annotation.min) MinErr(annotation)
        else null
    }
}
