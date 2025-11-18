package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.annotations.Pattern
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.PatternErr
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

public class PatternValidator(
    override val metadata: Metadata?,
    override val annotation: Pattern
) : ValidatorCond<CharSequence?, Pattern, PatternErr>(metadata, annotation) {
    override fun isValid(value: CharSequence?): PatternErr? {
        if (value == null) return null
        val toMatch = annotation.regex.toRegex()

        return if (toMatch.matches(value)) null
        else PatternErr(metadata, annotation)
    }
}