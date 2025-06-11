package io.github.brendonmendicino.aformvalidator.annotation

interface ValidatorCond<in T, out E : Any> {
    val conditions: List<(T) -> E?>
}
