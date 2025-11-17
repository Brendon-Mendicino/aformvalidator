package io.github.brendonmendicino.aformvalidator.annotation.error

public interface BindError<A : Annotation> {
    public val annotation: A
}