package io.github.brendonmendicino.aformvalidator.annotation

/**
 * A type should not be null.
 */
@Validator<ValidationError>(
    value = NotNull.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class NotNull() {
    public companion object {
        public class Validator : ValidatorCond<Any?, ValidationError> {
            override val conditions: List<(Any?) -> ValidationError?> = listOf {
                if (it == null) ValidationError.NotNull
                else null
            }
        }
    }
}
