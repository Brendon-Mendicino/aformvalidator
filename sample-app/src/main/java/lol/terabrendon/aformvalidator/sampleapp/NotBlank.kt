package lol.terabrendon.aformvalidator.sampleapp

import lol.terabrendon.aformvalidator.annotation.Validator
import lol.terabrendon.aformvalidator.annotation.ValidatorCond

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Validator<Int>(
    value = NotBlank.Companion.Validator::class,
    errorType = Int::class
)
annotation class NotBlank {

    companion object {
        class Validator : ValidatorCond<String?, Int> {
            override val conditions: List<(String?) -> Int?> = listOf { str ->
                if (str?.trim()?.isEmpty() != false) R.string.the_current_field_should_not_be_empty
                else null
            }

        }
    }
}