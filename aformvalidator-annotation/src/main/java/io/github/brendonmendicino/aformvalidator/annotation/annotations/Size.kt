package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.validators.SizeValidator
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.Validator
import kotlin.reflect.KClass

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
@Validator(SizeValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class Size(
    val metadata: KClass<out Metadata> = Nothing::class,
    val min: Int = 0,
    val max: Int = Int.MAX_VALUE,
)