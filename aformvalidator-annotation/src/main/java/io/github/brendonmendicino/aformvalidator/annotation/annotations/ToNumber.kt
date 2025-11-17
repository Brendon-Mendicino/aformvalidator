package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.Validator
import io.github.brendonmendicino.aformvalidator.annotation.validators.ToNumberValidator
import kotlin.reflect.KClass

/**
 * Validate a [String] to be a valid [Number].
 *
 * `null` is considered valid.
 *
 * # Examples
 *
 * ```
 * data class Person(
 *     val name: String,
 *     @ToNumber(Int::class)
 *     val age: String,
 * )
 *
 * val first = Person("First", "1")
 * println(first.age.error) // null
 * val second = Person("Second", "pluto")
 * println(second.age.error) // ToNumber(numberClass=Int::class)
 * ```
 */
@Validator(ToNumberValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class ToNumber(
    val numberClass: KClass<out Number>,
)