package lol.terabrendon.aformvalidator.annotation

import com.github.michaelbull.result.Result

interface ValidatorCond<T, E: Any> {
    val conditions: List<(T) -> E?>
}
