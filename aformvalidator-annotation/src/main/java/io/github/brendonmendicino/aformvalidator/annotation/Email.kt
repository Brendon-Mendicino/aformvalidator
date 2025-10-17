package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Validate a [String] as a valid email.
 *
 * Regex reference: [ref](https://www.regular-expressions.info/email.html)
 *
 * # Examples
 *
 * ```
 * @FormState
 * data class Person(
 *     val name: String,
 *     @Email
 *     val personal: String,
 *     // Custom pattern
 *     @Email(pattern="""\w+@mydomain\.com""")
 *     val work: String
 * )
 * ```
 */
@Validator<ValidationError>(
    value = Email.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class Email(
    val pattern: String = """\A[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\z"""
) {
    public companion object {
        public class Validator(pattern: String): ValidatorCond<String?, ValidationError> {
            override val conditions: List<(String?) -> ValidationError?> = listOf {
                val toMatch = pattern.toRegex()

                if (it == null || !toMatch.matches(it)) ValidationError.Email
                else null
            }
        }
    }
}
