package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Validate a [Collection] size with upper and lower bounds.
 *
 * # Examples
 *
 * ```
 * data class BoundList(
 *     @Size(2, 10)
 *     val list: List<Int>,
 * )
 *
 * val empty = BoundList(listOf())
 * println(empty.list.error) // Size(min=2, max=10)
 * val withElements = BoundList(listOf(1, 2, 3))
 * println(withElements.list.error) // null
 * ```
 */
@Validator<ValidationError>(
    value = Size.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class Size(
    val min: Int = 0,
    val max: Int = Int.MAX_VALUE,
) {
    public companion object {
        public class Validator(
            public val min: Int,
            public val max: Int,
        ) : ValidatorCond<Collection<*>, ValidationError> {
            override val conditions: List<(Collection<*>) -> ValidationError?> = listOf {
                if (min <= it.size && it.size <= max) null
                else ValidationError.Size(min = min, max = max)
            }
        }
    }
}
