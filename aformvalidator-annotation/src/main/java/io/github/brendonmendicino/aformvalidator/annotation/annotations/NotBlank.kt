package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.validators.NotBlankValidator
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.Validator
import kotlin.reflect.KClass

/**
 * Validate a [String]. It needs to have at least one non-white character.
 *
 * `null` is considered valid.
 *
 * # Examples
 *
 * ```
 * @FormState
 * data class Person(@NotBlank val name: String? = null)
 *
 * var person = Person().toValidator()
 * println(person.name.error) // NotBlank
 * person = person.copy(name = person.name.update("   "))
 * println(person.name.error) // NotBlank
 * person = person.copy(name = person.name.update("pippo"))
 * println(person.name.error) // null
 * ```
 */
@Validator(NotBlankValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class NotBlank(
    val metadata: KClass<out Metadata> = Nothing::class,
)