package io.github.brendonmendicino.aformvalidator.annotation

@Validator<ValidationError>(
    value = Pattern.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Repeatable
@MustBeDocumented
public annotation class Pattern(
    public val regex: String = ""
) {
    public companion object {
        public class Validator(public val regex: String) : ValidatorCond<String?, ValidationError> {
            override val conditions: List<(String?) -> ValidationError?> = listOf {
                val toMatch = regex.toRegex()

                if (it == null || !toMatch.matches(it)) ValidationError.Pattern
                else null
            }
        }
    }
}
