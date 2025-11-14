package io.github.brendonmendicino.aformvalidator.annotation

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
@Validator<ValidationError>(
    value = NotBlank.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class NotBlank {

    public companion object {
        public class Validator : ValidatorCond<String?, ValidationError> {
            override val conditions: List<(String?) -> ValidationError?> = listOf { str ->
                if (str == null) null
                else if (str.isBlank()) ValidationError.NotBlank
                else null
            }
        }
    }
}