package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.annotations.FormState

@FormState
data class TestState(
    val name: String? = "",
    val hello: String? = "",
)