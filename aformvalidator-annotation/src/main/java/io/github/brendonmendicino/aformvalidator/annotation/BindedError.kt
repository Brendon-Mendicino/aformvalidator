package io.github.brendonmendicino.aformvalidator.annotation

public interface BindError<A : Annotation> {
    public val annotation: A
}