package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Size
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.SizeErr

public class SizeValidator(
    override val annotation: Size
) : ValidatorCond<Any?, Size, SizeErr>(annotation) {
    override fun isValid(value: Any?): SizeErr? {
        val valid = when (value) {
            is Collection<*> -> annotation.min <= value.size && value.size <= annotation.max
            is CharSequence -> annotation.min <= value.length && value.length <= annotation.max
            is Map<*, *> -> annotation.min <= value.size && value.size <= annotation.max
            else -> true
        }

        return if (valid) null
        else SizeErr(annotation)
    }
}