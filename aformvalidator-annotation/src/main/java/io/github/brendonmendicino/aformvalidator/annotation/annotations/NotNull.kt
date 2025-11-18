package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.Validator
import io.github.brendonmendicino.aformvalidator.annotation.validators.NotNullValidator

/**
 * A type should not be null.
 */
@Validator(NotNullValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class NotNull
