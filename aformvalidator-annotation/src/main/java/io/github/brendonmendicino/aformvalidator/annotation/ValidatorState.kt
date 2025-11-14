package io.github.brendonmendicino.aformvalidator.annotation

public interface ValidatorState<out E: Any> {
    public val used: Boolean
    public val error: E?
    public val isError: Boolean
}