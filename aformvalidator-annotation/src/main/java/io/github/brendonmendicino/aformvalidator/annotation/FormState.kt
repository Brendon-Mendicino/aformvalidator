package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Annotate your class with [FormState] to generate the respective validator class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class FormState
