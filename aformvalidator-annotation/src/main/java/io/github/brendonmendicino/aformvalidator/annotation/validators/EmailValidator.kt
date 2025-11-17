package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.Email
import io.github.brendonmendicino.aformvalidator.annotation.ValidationError.EmailErr
import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond

public class EmailValidator(override val annotation: Email) :
    ValidatorCond<CharSequence?, Email, EmailErr>(annotation) {
    override fun isValid(value: CharSequence?): EmailErr? {
        if (value == null) return null

        val toMatch = annotation.pattern.toRegex()

        return if (toMatch.matches(value)) null
        else EmailErr(annotation)
    }
}