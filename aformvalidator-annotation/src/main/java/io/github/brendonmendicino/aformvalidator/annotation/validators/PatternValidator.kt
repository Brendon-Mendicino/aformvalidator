package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Pattern
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.PatternErr

public class PatternValidator(
    override val annotation: Pattern
) : ValidatorCond<CharSequence?, Pattern, PatternErr>(annotation) {
    override fun isValid(value: CharSequence?): PatternErr? {
        if (value == null) return null
        val toMatch = annotation.regex.toRegex()

        return if (toMatch.matches(value)) null
        else PatternErr(annotation)
    }
}