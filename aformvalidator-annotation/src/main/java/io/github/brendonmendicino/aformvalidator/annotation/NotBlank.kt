package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Validate a [String]. It needs to have at least one character after trimming the [String].
 */
@Validator<ValidationError>(
    value = NotBlank.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class NotBlank {

    public companion object {
        public class Validator : ValidatorCond<String?, ValidationError> {
            override val conditions: List<(String?) -> ValidationError?> = listOf { str ->
                if (str?.trim()?.isEmpty() != false) ValidationError.NotBlank
                else null
            }
        }
    }
}