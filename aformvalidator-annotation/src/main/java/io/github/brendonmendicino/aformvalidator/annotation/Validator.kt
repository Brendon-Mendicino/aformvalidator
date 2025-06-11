package io.github.brendonmendicino.aformvalidator.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Validator<E : Any>(
    val value: KClass<out ValidatorCond<*, E>>,
    val errorType: KClass<out E>,
)