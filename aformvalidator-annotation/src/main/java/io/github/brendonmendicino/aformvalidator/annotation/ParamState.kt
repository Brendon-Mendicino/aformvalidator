package io.github.brendonmendicino.aformvalidator.annotation

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getErrorOr
import com.github.michaelbull.result.mapResult

public data class ParamState<T, out E : Any>(
    public val value: T,
    public val conditions: List<(T) -> E?> = emptyList(),
    public override val used: Boolean = false,
) : ValidatorState<E> {
    public override val error: E?
        get() = conditions
            .mapResult { condition -> condition(value)?.let { Err(it) } ?: Ok(Unit) }
            .getErrorOr(null)

    public override val isError: Boolean = used && error != null

    public fun update(value: T = this.value): ParamState<T, E> = ParamState(
        value = value,
        conditions = this.conditions,
        used = true,
    )
}
