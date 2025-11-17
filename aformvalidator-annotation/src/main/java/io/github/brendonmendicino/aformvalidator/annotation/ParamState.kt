package io.github.brendonmendicino.aformvalidator.annotation

public data class ParamState<T, out E : Any>(
    public val value: T,
    public val conditions: List<ValidatorCond<T, *, E>> = emptyList(),
    public val used: Boolean = false,
) {
    public val error: E?
        get() = conditions
            .asSequence()
            .mapNotNull { cond -> cond.isValid(value) }
            .firstOrNull()

    public val isError: Boolean = used && error != null

    public fun update(value: T = this.value): ParamState<T, E> = ParamState(
        value = value,
        conditions = this.conditions,
        used = true,
    )
}
