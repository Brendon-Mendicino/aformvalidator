package io.github.brendonmendicino.aformvalidator.annotation

import io.github.brendonmendicino.aformvalidator.annotation.validators.MinDoubleValidator

/**
 * Same as [Min] but the value can be a [Double].
 */
@Validator(MinDoubleValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class MinDouble(
    val min: Double
)