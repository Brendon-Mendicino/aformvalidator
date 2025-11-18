package io.github.brendonmendicino.aformvalidator.annotation.annotations

import io.github.brendonmendicino.aformvalidator.annotation.validators.EmailValidator
import io.github.brendonmendicino.aformvalidator.core.Validator
import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass

/**
 * Validate a [String] as a valid email.
 *
 * Regex reference: [ref](https://www.regular-expressions.info/email.html)
 *
 * `null` is considered valid
 *
 * # Examples
 *
 * ```
 * @FormState
 * data class Person(
 *     val name: String,
 *     @Email
 *     val personal: String,
 *     // Custom pattern
 *     @Email(pattern="""\w+@mydomain\.com""")
 *     val work: String
 * )
 * ```
 */
@Validator(EmailValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class Email(
    val metadata: KClass<out Metadata> = Nothing::class,
    @Language("RegExp")
    val pattern: String = """\A[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\z"""
)
