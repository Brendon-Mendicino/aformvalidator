package io.github.brendonmendicino.aformvalidator.sampleapp

import android.annotation.SuppressLint
import io.github.brendonmendicino.aformvalidator.core.FormState
import io.github.brendonmendicino.aformvalidator.core.Metadata
import io.github.brendonmendicino.aformvalidator.core.Validator
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond
import java.text.SimpleDateFormat
import kotlin.reflect.KClass

class LogMessageValidator(
    override val metadata: Metadata?,
    override val annotation: LogMessage,
) : ValidatorCond<String?, LogMessage, String>(metadata, annotation) {
    @SuppressLint("SimpleDateFormat")
    override fun isValid(value: String?): String? {
        val input = value
        if (input == null) return null

        if (input.split(" ").size != annotation.sections) return "Section check failed!"

        runCatching { SimpleDateFormat(annotation.timestamp).parse(input.split(" ")[0]) }
            .let { if (it.isFailure) return "Timestamp check failed!" }

        if (!annotation.allowedModules.contains(input.split(" ")[1])) return "Module check failed!"

        if (input.split(" ")[2].length > annotation.maxLogLen) return "Log length check failed!"

        return null
    }
}

@Validator(LogMessageValidator::class)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class LogMessage(
    val metadata: KClass<out Metadata> = Nothing::class,
    val sections: Int = 3,
    val timestamp: String = "YYYY-MM-DD",
    val allowedModules: Array<String> = ["main", "test"],
    val maxLogLen: Int = 100,
)

@FormState
data class MyLog(
    @LogMessage
    val line: String,
)