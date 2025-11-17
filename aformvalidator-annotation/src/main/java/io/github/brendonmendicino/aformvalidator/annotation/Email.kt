package io.github.brendonmendicino.aformvalidator.annotation

import io.github.brendonmendicino.aformvalidator.annotation.validators.EmailValidator
import org.intellij.lang.annotations.Language

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
    @Language("RegExp")
    val pattern: String = """\A[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\z"""
)
