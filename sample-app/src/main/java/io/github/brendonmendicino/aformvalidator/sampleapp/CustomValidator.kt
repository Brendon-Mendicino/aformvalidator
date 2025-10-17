package io.github.brendonmendicino.aformvalidator.sampleapp

import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.Validator
import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond
import java.text.SimpleDateFormat

// The constructor parameters are the same that the annotation
// takes.
class LogMessageValidator(
    val sections: Int,
    val timestamp: String,
    val allowedModules: Array<String>,
    val maxLogLen: Int,
) : ValidatorCond<String, String> {
    override val conditions: List<(String) -> String?> = listOf(
        { input -> if (input.split(" ").size == sections) null else "Section check failed!" },
        { input ->
            runCatching { SimpleDateFormat(timestamp).parse(input.split(" ")[0]) }
                .let { if (it.isSuccess) null else "Timestamp check failed!" }
        },
        { input ->
            if (allowedModules.contains(input.split(" ")[1])) null
            else "Module check failed!"
        },
        { input ->
            if (input.split(" ")[2].length <= maxLogLen) null
            else "Log length check failed!"
        },
    )
}

@Validator<String>(
    value = LogMessageValidator::class,
    errorType = String::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class LogMessage(
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