package io.github.brendonmendicino.aformvalidator.annotation


/**
 * Validate a [Comparable] against a [min] value.
 */
@Validator<ValidationError>(
    value = Min.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class Min(
    val min: Long
) {
    public companion object {
        public class Validator(public val min: Long) :
            ValidatorCond<Number, ValidationError> {
            override val conditions: List<(Number) -> ValidationError?> = listOf {
                Long
                if (it.toLong() < min) ValidationError.Min(min = min.toLong())
                else null
            }

        }
    }
}
