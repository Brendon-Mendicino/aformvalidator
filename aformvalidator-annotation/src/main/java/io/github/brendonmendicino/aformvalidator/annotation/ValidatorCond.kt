package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Set of conditions that a [ParamState] has to satisfy.
 */
public abstract class ValidatorCond<in T, A : Annotation, out E : Any>(
    public open val annotation: A,
) {
    public abstract fun isValid(value: T): E?
}
