package io.github.brendonmendicino.aformvalidator.annotation

@Validator<ValidationError>(
    value = Size.Companion.Validator::class,
    errorType = ValidationError::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@MustBeDocumented
public annotation class Size(
    val min: Int = 0,
    val max: Int = Int.MAX_VALUE,
) {
    public companion object {
        public class Validator(
            public val min: Int,
            public val max: Int,
        ) : ValidatorCond<Collection<*>, ValidationError> {
            override val conditions: List<(Collection<*>) -> ValidationError?> = listOf {
                if (min <= it.size && it.size <= max) null
                else ValidationError.Size
            }
        }
    }
}
