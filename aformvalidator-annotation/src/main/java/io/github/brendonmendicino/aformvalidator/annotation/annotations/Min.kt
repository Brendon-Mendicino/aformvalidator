package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.validators.MinValidator
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.Validator
import kotlin.reflect.KClass


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
@Validator(MinValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Repeatable
@MustBeDocumented
public annotation class Min(
    val metadata: KClass<out Metadata> = Nothing::class,
    val min: Long
)