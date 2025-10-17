package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Same as [Max] but the value can be a [Double].
 */
@Validator<ValidationError>(
    value = MaxDouble.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class MaxDouble(
    val max: Double
) {
    public companion object {
        public class Validator(public val max: Double) :
            ValidatorCond<Number?, ValidationError> {
            override val conditions: List<(Number?) -> ValidationError?> = listOf {
                it ?: return@listOf null

                if (it.toDouble() > max) ValidationError.MaxDouble(max = max)
                else null
            }

        }
    }
}
