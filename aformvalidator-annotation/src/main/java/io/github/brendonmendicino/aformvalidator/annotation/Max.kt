package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Validate a [Comparable] against a [max] value.
 */
@Validator<ValidationError>(
    value = Max.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class Max(
    val max: Long
) {
    public companion object {
        public class Validator(public val max: Long) :
            ValidatorCond<Number?, ValidationError> {
            override val conditions: List<(Number?) -> ValidationError?> = listOf {
                it ?: return@listOf null

                if (it.toLong() > max) ValidationError.Min(min = max)
                else null
            }

        }
    }
}
