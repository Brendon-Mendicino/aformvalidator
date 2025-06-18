package io.github.brendonmendicino.aformvalidator.annotation

import kotlin.reflect.KClass

public sealed class ValidationError {
    public object NotBlank : ValidationError()
    public data class Size(val min: Int, val max: Int) : ValidationError()
    public data class Pattern(val regex: String) : ValidationError()
    public object Email : ValidationError()
    public data class Min(val min: Long) : ValidationError()
    public data class Max(val max: Long) : ValidationError()
    public data class MinDouble(val min: Double) : ValidationError()
    public data class MaxDouble(val max: Double) : ValidationError()
    public data class ToNumber(val numberClass: KClass<out Number>) : ValidationError()
    public object NotNull : ValidationError()
}