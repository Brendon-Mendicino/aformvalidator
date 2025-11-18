package io.github.brendonmendicino.aformvalidator.annotation.validators

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