package io.github.brendonmendicino.aformvalidator.annotation

public interface ValidatorCond<in T, out E : Any> {
    public val conditions: List<(T) -> E?>
}
