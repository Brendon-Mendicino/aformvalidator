package io.github.brendonmendicino.aformvalidator.annotation

import kotlin.reflect.KClass

/**
 * [Validator] is the base annotation to perform validations of a [ParamState].
 *
 * All the base annotations of the library extend from this one.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Repeatable
public annotation class Validator<E : Any>(
    val value: KClass<out ValidatorCond<*, E>>,
    val errorType: KClass<out E>,
)