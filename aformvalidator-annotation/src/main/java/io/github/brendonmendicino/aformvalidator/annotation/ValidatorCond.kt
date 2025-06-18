package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Set of conditions that a [ParamState] has to satisfy.
 */
public interface ValidatorCond<in T, out E : Any> {
    public val conditions: List<(T) -> E?>
}
