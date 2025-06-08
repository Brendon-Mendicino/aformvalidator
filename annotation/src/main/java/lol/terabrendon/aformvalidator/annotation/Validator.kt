package lol.terabrendon.aformvalidator.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Validator<E : Any>(
    val value: KClass<out ValidatorCond<*, E>>,
    val errorType: KClass<E>,
)