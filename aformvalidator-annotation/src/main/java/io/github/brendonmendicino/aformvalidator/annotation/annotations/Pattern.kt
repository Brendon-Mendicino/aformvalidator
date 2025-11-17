package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.Validator
import io.github.brendonmendicino.aformvalidator.annotation.validators.PatternValidator
import org.intellij.lang.annotations.Language

/**
 * Validate a [String] against a [regex] pattern.
 */
@Validator(PatternValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class Pattern(
    @Language("RegExp")
    val regex: String = ""
)