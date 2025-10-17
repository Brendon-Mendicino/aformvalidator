package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Check if a [Number] is greater than [max]. The validator
 * only checks against the value of the [Number], it its value
 * is `null` the check passes.
 *
 * # Examples
 *
 * ```
 * @FormState
 * data class Point(
 *     @Max(10)
 *     val x: Int,
 *     @Max(10)
 *     val y: Int?,
 * )
 *
 * var state = Point(20, null).toValidator()
 * println(state.x.error) // Max(max=10)
 * state = state.copy(x = state.x.update(5))
 * println(state.errors.firstOrNull()) // null
 * ```
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

                if (it.toLong() > max) ValidationError.Max(max = max)
                else null
            }

        }
    }
}
