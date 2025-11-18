package io.github.brendonmendicino.aformvalidator.core

import kotlin.reflect.KClass

/**
 * [Validator] is the base annotation to perform validations of a [ParamState].
 *
 * All the base annotations of the library extend from this one.
 *
 * # Examples
 *
 * ```
 * class LogMessageValidator(
 *     override val annotation: LogMessage,
 * ) : ValidatorCond<String?, LogMessage, String>(annotation) {
 *     @SuppressLint("SimpleDateFormat")
 *     override fun isValid(value: String?): String? {
 *         val input = value
 *         if (input == null) return null
 *
 *         if (input.split(" ").size != annotation.sections) return "Section check failed!"
 *
 *         runCatching { SimpleDateFormat(annotation.timestamp).parse(input.split(" ")[0]) }
 *             .let { if (it.isFailure) return "Timestamp check failed!" }
 *
 *         if (!annotation.allowedModules.contains(input.split(" ")[1])) return "Module check failed!"
 *
 *         if (input.split(" ")[2].length > annotation.maxLogLen) return "Log length check failed!"
 *
 *         return null
 *     }
 * }
 *
 * @Validator(LogMessageValidator::class)
 * @Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
 * @Retention(AnnotationRetention.SOURCE)
 * annotation class LogMessage(
 *     val sections: Int = 3,
 *     val timestamp: String = "YYYY-MM-DD",
 *     val allowedModules: Array<String> = ["main", "test"],
 *     val maxLogLen: Int = 100,
 * )
 *
 * @FormState
 * data class MyLog(
 *     @LogMessage
 *     val line: String,
 * )
 * ```
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Repeatable
public annotation class Validator(
    vararg val validatedBy: KClass<out ValidatorCond<*, *, *>>,
)