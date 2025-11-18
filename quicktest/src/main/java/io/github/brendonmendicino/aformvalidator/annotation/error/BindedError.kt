package io.github.brendonmendicino.aformvalidator.annotation.error

import io.github.brendonmendicino.aformvalidator.core.Metadata

public interface BindError<A : Annotation, M : Metadata> {
    public val metadata: M?
    public val annotation: A
}