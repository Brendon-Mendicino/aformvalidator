package io.github.brendonmendicino.aformvalidator.annotation

@Validator<ValidationError>(
    value = NotBlank.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class NotBlank {

    companion object {
        class Validator : ValidatorCond<String?, ValidationError> {
            override val conditions: List<(String?) -> ValidationError?> = listOf { str ->
                if (str?.trim()?.isEmpty() != false) ValidationError.NotBlank
                else null
            }

        }
    }
}