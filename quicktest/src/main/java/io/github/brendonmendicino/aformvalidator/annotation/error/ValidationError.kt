package io.github.brendonmendicino.aformvalidator.annotation.error

import io.github.brendonmendicino.aformvalidator.annotation.annotations.Pattern
import io.github.brendonmendicino.aformvalidator.core.Metadata

public sealed interface ValidationError<A : Annotation, M : Metadata> : BindError<A, M> {
    public data class PatternErr<M: Metadata>(override val metadata: M?, override val annotation: Pattern) : ValidationError<Pattern, M>
}