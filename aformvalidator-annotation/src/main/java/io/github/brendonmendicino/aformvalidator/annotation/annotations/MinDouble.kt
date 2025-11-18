package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.validators.MinDoubleValidator
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.Validator
import kotlin.reflect.KClass

/**
 * Same as [Min] but the value can be a [Double].
 */
@Validator(MinDoubleValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class MinDouble(
    val metadata: KClass<out Metadata> = Nothing::class,
    val min: Double
)