package io.github.brendonmendicino.aformvalidator.annotation

import kotlin.reflect.KClass

/**
 * Validate a [String] to be a valid [Number].
 *
 * # Examples
 *
 * ```
 * data class Person(
 *     val name: String,
 *     @ToNumber(Int::class)
 *     val age: String,
 * )
 *
 * val first = Person("First", "1")
 * println(first.age.error) // null
 * val second = Person("Second", "pluto")
 * println(second.age.error) // ToNumber(numberClass=Int::class)
 * ```
 */
@Validator<ValidationError>(
    value = ToNumber.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class ToNumber(
    val numberClass: KClass<out Number>
) {
    public companion object {
        public class Validator(public val numberClass: KClass<out Number>) :
            ValidatorCond<String?, ValidationError> {
            override val conditions: List<(String?) -> ValidationError?> = listOf {
                it ?: return@listOf null
                if (it == "") return@listOf null

                if (numberClass.toNumberOrNull(it) == null) ValidationError.ToNumber(numberClass = numberClass)
                else null
            }
        }

        private fun KClass<out Number>.toNumberOrNull(number: String): Number? = when (this) {
            Byte::class -> number.toByteOrNull()
            Short::class -> number.toShortOrNull()
            Int::class -> number.toIntOrNull()
            Long::class -> number.toLongOrNull()
            Float::class -> number.toFloatOrNull()
            Double::class -> number.toDoubleOrNull()
            else -> null
        }
    }
}
