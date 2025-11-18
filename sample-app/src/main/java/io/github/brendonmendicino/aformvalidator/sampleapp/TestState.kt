package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.FormState

@FormState
data class TestState(
    val name: String? = "",
    val hello: String? = "",
)