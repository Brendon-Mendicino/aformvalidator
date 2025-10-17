package io.github.brendonmendicino.aformvalidator.annotation

import java.text.SimpleDateFormat
import kotlin.reflect.KClass

/**
 * [Validator] is the base annotation to perform validations of a [ParamState].
 *
 * All the base annotations of the library extend from this one.
 *
 * # Examples
 *
 * ```
 * // The constructor parameters are the same that the annotation
 * // takes.
 * class LogMessageValidator(
 *     val sections: Int,
 *     val timestamp: String,
 *     val allowedModules: Array<String>,
 *     val maxLogLen: Int,
 * ): ValidatorCond<String, String> {
 *     override val conditions: List<(String) -> String?> = listOf(
 *         { input -> input.split(" ").size == sections) null else "Section check failed!" },
 *         { input ->
 *             runCatching { SimpleDateFormat(timestamp).parse(input.split(" ")[0]) }
 *                 .let { if (is.isSuccess) null else "Timestamp check failed!" }
 *         },
 *         { input ->
 *             if (allowedModules.contains(input.split(" ")[1])) null
 *             else "Module check failed!"
 *         },
 *         { input ->
 *             if (input.split(" ")[2].length <= maxLogLen) null
 *             else "Log length check failed!"
 *         },
 *     )
 * }
 *
 * @Validator<String>(
 *     value = LogMessageValidator::class,
 *     errorType = String::class,
 * )
 * @Target(PROPERTY, ANNOTATION_CLASS)
 * @Retention(SOURCE)
 * annotation class LogMessage(
 *     val sections: Int = 3,
 *     val timestamp: String = "YYYY-MM-DD",
 *     val allowedModules: Array<String> = ["main", "test"],
 *     val maxLogLen: Int = 100,
 * )
 * ```
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Repeatable
public annotation class Validator<E : Any>(
    val value: KClass<out ValidatorCond<*, E>>,
    val errorType: KClass<out E>,
)