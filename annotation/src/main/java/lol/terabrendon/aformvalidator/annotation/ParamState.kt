package lol.terabrendon.aformvalidator.annotation

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getErrorOr
import com.github.michaelbull.result.mapResult

class ParamState<T, E : Any>(
    value: T,
    val conditions: List<(T) -> E?> = emptyList(),
    used: Boolean = false,
) {
    var used = used
        private set

    var value = value
        private set(newValue) {
            used = true
            field = newValue
        }

    val error: E?
        get() = conditions
            .mapResult { condition -> condition(value)?.let { Err(it) } ?: Ok(Unit) }
            .getErrorOr(null)

    val isError = used && error != null

    fun copy(
        value: T = this.value,
        conditions: List<(T) -> E?> = this.conditions,
        used: Boolean = this.used
    ): ParamState<T, E> = ParamState(
        value = value,
        conditions = conditions,
        used = used,
    )
}
