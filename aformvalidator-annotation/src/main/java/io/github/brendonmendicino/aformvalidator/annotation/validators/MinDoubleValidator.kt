package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.annotations.MinDouble
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.MinDoubleErr
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

public class MinDoubleValidator(
    override val metadata: Metadata?, override val annotation: MinDouble
) : ValidatorCond<Number?, MinDouble, MinDoubleErr>(metadata, annotation) {
    override fun isValid(value: Number?): MinDoubleErr? {
        if (value == null) return null

        return if (value.toDouble() < annotation.min) MinDoubleErr(metadata, annotation)
        else null
    }
}
