package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Same as [Min] but the value can be a [Double].
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
            ValidatorCond<Number?, ValidationError> {
            override val conditions: List<(Number?) -> ValidationError?> = listOf {
                it ?: return@listOf null

                if (it.toDouble() < min) ValidationError.MinDouble(min = min)
                else null
            }

        }
    }
}
