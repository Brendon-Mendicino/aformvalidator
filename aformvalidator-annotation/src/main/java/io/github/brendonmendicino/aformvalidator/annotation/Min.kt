package io.github.brendonmendicino.aformvalidator.annotation


/**
 * Check if a [Number] is smaller than [min]. The validator
 * only checks against the value of the [Number], it its value
 * is `null` the check passes.
 *
 * `null` is considered valid.
 *
 * # Examples
 *
 * ```
 * @FormState
 * data class Point(
 *     @Min(0)
 *     val x: Int,
 *     @Min(0)
 *     val y: Int?,
 * )
 *
 * var state = Point(-10, null).toValidator()
 * println(state.x.error) // Min(min=0)
 * state = state.copy(x = state.x.update(5))
 * println(state.errors.firstOrNull()) // null
 * ```
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
            ValidatorCond<Number?, ValidationError> {
            override val conditions: List<(Number?) -> ValidationError?> = listOf {
                it ?: return@listOf null

                if (it.toLong() < min) ValidationError.Min(min = min)
                else null
            }

        }
    }
}
