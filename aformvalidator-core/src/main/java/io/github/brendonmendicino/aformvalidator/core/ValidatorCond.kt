package io.github.brendonmendicino.aformvalidator.core

/**
 * Set of conditions that a [ParamState] has to satisfy.
 */
public abstract class ValidatorCond<in T, A : Annotation, M: Metadata, out E : Any>(
    public open val metadata: M? = null,
    public open val annotation: A,
) {
    public abstract fun isValid(value: T): E?
}