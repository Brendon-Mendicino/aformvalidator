package io.github.brendonmendicino.aformvalidator.annotation

/**
 * A type should not be null.
 */
@Validator()
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class NotNull
