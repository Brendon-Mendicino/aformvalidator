package io.github.brendonmendicino.aformvalidator.annotation.validators

import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond
import io.github.brendonmendicino.aformvalidator.annotation.annotations.ToNumber
import io.github.brendonmendicino.aformvalidator.annotation.error.ValidationError.ToNumberErr
import java.math.BigDecimal
import java.math.BigInteger

public class ToNumberValidator(
    override val annotation: ToNumber
) : ValidatorCond<String?, ToNumber, ToNumberErr>(annotation) {
    override fun isValid(value: String?): ToNumberErr? {
        if (value == null) return null
        if (value.isBlank()) return null

        val num = when (annotation.numberClass) {
            Byte::class -> value.toByteOrNull()
            Short::class -> value.toShortOrNull()
            Int::class -> value.toIntOrNull()
            Long::class -> value.toLongOrNull()
            Float::class -> value.toFloatOrNull()
            Double::class -> value.toDoubleOrNull()
            BigInteger::class -> value.toBigIntegerOrNull()
            BigDecimal::class -> value.toBigDecimalOrNull()
            else -> return null
        }

        return if (num != null) null
        else ToNumberErr(annotation)
    }
}