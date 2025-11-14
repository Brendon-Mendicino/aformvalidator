package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Validate with upper and lower bounds.
 *
 * Supported types:
 *
 * - [Collection]
 * - [Map]
 * - [CharSequence]
 *
 * If a type is none of the previous, the value is considered valid.
 *
 * `null` is considered valid.
 *
 * # Examples
 *
 * ```
 * data class BoundList(
 *     @Size(min=2, max=10)
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
        ) : ValidatorCond<Any, ValidationError> {
            override val conditions: List<(Any) -> ValidationError?> = listOf(
                {
                    if (it !is Collection<*>) null
                    else if (min <= it.size && it.size <= max) null
                    else ValidationError.Size(min = min, max = max)
                },
                {
                    if (it !is CharSequence) null
                    else if (min <= it.length && it.length <= max) null
                    else ValidationError.Size(min = min, max = max)
                },
                {
                    if (it !is Map<*, *>) null
                    else if (min <= it.size && it.size <= max) null
                    else ValidationError.Size(min = min, max = max)
                },
            )
        }
    }
}
