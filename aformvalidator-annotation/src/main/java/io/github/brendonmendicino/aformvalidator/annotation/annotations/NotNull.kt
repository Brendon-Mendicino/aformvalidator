package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.Validator

/**
 * A type should not be null.
 */
@Validator()
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class NotNull
