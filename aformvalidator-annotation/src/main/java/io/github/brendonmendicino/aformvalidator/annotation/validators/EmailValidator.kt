package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.annotations.Email
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.EmailErr
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

public class EmailValidator(
    override val metadata: Metadata?,
    override val annotation: Email
) : ValidatorCond<CharSequence?, Email, EmailErr>(metadata, annotation) {
    override fun isValid(value: CharSequence?): EmailErr? {
        if (value == null) return null

        val toMatch = annotation.pattern.toRegex()

        return if (toMatch.matches(value)) null
        else EmailErr(metadata, annotation)
    }
}