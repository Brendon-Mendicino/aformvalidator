package io.github.brendonmendicino.aformvalidator.annotation.error

import io.github.brendonmendicino.aformvalidator.annotation.annotations.Email
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Max
import io.github.brendonmendicino.aformvalidator.annotation.annotations.MaxDouble
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Min
import io.github.brendonmendicino.aformvalidator.annotation.annotations.MinDouble
import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotBlank
import io.github.brendonmendicino.aformvalidator.annotation.annotations.NotNull
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Pattern
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Size
import io.github.brendonmendicino.aformvalidator.annotation.annotations.ToNumber

public sealed interface ValidationError<A : Annotation> : BindError<A> {
    public data class NotBlankErr(override val annotation: NotBlank) : ValidationError<NotBlank>
    public data class SizeErr(override val annotation: Size) : ValidationError<Size>
    public data class PatternErr(override val annotation: Pattern) : ValidationError<Pattern>
    public data class EmailErr(override val annotation: Email) : ValidationError<Email>
    public data class MinErr(override val annotation: Min) : ValidationError<Min>
    public data class MaxErr(override val annotation: Max) : ValidationError<Max>
    public data class MinDoubleErr(override val annotation: MinDouble) : ValidationError<MinDouble>
    public data class MaxDoubleErr(override val annotation: MaxDouble) : ValidationError<MaxDouble>
    public data class ToNumberErr(override val annotation: ToNumber) : ValidationError<ToNumber>
    public data class NotNullErr(override val annotation: NotNull) : ValidationError<NotNull>
}