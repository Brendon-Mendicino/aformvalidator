package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.validators.PatternValidator
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.Validator
import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass

/**
 * Validate a [String] against a [regex] pattern.
 */
@Validator(PatternValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class Pattern(
    val metadata: KClass<out Metadata>,
    @Language("RegExp")
    val regex: String = "",
)