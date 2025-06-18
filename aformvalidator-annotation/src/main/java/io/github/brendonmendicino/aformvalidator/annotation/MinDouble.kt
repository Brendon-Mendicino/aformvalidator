package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Validate a [Comparable] against a [min] value.
 */
@Validator<ValidationError>(
    value = MinDouble.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class MinDouble(
    val min: Double
) {
    public companion object {
        public class Validator(public val min: Double) :
            ValidatorCond<Number, ValidationError> {
            override val conditions: List<(Number) -> ValidationError?> = listOf {
                if (it.toDouble() < min) ValidationError.MinDouble(min = min)
                else null
            }

        }
    }
}
