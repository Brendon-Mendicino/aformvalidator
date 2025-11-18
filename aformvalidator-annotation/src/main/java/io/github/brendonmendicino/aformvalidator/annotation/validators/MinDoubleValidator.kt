package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.annotations.MinDouble
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.MinDoubleErr
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

public class MinDoubleValidator(override val annotation: MinDouble) :
    ValidatorCond<Number?, MinDouble, MinDoubleErr>(annotation) {
    override fun isValid(value: Number?): MinDoubleErr? {
        if (value == null) return null

        return if (value.toDouble() < annotation.min) MinDoubleErr(annotation)
        else null
    }
}
