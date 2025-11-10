package io.github.brendonmendicino.aformvalidator.annotation

import org.intellij.lang.annotations.Language

/**
 * Validate a [String] against a [regex] pattern.
 */
@Validator<ValidationError>(
    value = Pattern.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class Pattern(
    @Language("RegExp")
    public val regex: String = ""
) {
    public companion object {
        public class Validator(public val regex: String) : ValidatorCond<String?, ValidationError> {
            override val conditions: List<(String?) -> ValidationError?> = listOf {
                val toMatch = regex.toRegex()

                if (it == null || !toMatch.matches(it)) ValidationError.Pattern(regex = regex)
                else null
            }
        }
    }
}
