package io.github.brendonmendicino.aformvalidator.annotation

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
    public val regex: String = ""
)