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

//public sealed interface ValidationError<A : Annotation, M : Metadata> : BindError<A, M> {
//    public data class NotBlankErr<M : Metadata>(override val metadata: M?, override val annotation: NotBlank) : ValidationError<NotBlank, M>
//    public data class SizeErr<M: Metadata>(override val metadata: M?, override val annotation: Size) : ValidationError<Size, M>
//    public data class PatternErr<M: Metadata>(override val metadata: M?, override val annotation: Pattern) : ValidationError<Pattern, M>
//    public data class EmailErr<M: Metadata>(override val metadata: M?, override val annotation: Email) : ValidationError<Email, M>
//    public data class MinErr<M: Metadata>(override val metadata: M?, override val annotation: Min) : ValidationError<Min, M>
//    public data class MaxErr<M: Metadata>(override val metadata: M?, override val annotation: Max) : ValidationError<Max, M>
//    public data class MinDoubleErr<M: Metadata>(override val metadata: M?, override val annotation: MinDouble) : ValidationError<MinDouble, M>
//    public data class MaxDoubleErr<M: Metadata>(override val metadata: M?, override val annotation: MaxDouble) : ValidationError<MaxDouble, M>
//    public data class ToNumberErr<M: Metadata>(override val metadata: M?, override val annotation: ToNumber) : ValidationError<ToNumber, M>
//    public data class NotNullErr<M: Metadata>(override val metadata: M?, override val annotation: NotNull) : ValidationError<NotNull, M>
//}

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