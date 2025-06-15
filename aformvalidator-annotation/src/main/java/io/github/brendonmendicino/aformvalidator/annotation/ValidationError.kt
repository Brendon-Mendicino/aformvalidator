package io.github.brendonmendicino.aformvalidator.annotation

public sealed class ValidationError {
    public object NotBlank: ValidationError()
    public object Size: ValidationError()
    public object Pattern: ValidationError()
}