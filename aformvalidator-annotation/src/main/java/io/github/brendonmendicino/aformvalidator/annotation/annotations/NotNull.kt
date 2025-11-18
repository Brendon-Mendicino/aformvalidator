package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.validators.NotNullValidator
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.Validator
import kotlin.reflect.KClass

/**
 * A type should not be null.
 */
@Validator(NotNullValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class NotNull(
    val metadata: KClass<out Metadata> = Nothing::class,
)
