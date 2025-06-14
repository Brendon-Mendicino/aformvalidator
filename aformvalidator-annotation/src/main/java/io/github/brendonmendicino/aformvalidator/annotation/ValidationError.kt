package io.github.brendonmendicino.aformvalidator.annotation

sealed class ValidationError {
    object NotBlank: ValidationError()
    object Size: ValidationError()
    object Pattern: ValidationError()
}