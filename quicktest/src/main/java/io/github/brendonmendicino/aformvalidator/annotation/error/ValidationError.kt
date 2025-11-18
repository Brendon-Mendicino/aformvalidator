package io.github.brendonmendicino.aformvalidator.annotation.error

import io.github.brendonmendicino.aformvalidator.annotation.annotations.Pattern
import io.github.brendonmendicino.aformvalidator.core.Metadata

public sealed interface ValidationError<A : Annotation> : BindError<A, Metadata> {
    public data class PatternErr(override val metadata: Metadata?, override val annotation: Pattern) : ValidationError<Pattern>
}