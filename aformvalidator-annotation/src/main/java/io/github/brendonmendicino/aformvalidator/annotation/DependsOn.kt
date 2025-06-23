package io.github.brendonmendicino.aformvalidator.annotation

/**
 * Mark that a property depends on other properties of the class.
 *
 * Used to correctly updated the [ParamState.used] parameter.
 *
 * # Examples
 *
 * ```
 * @FormState
 * data class Form(
 *     @ToNumber(Int::class)
 *     val numStr: String = "",
 * ) {
 *     @Min(0)
 *     @DependsOn(["numStr"])
 *     val num: Int? = numStr.toIntOrNull()
 * }
 * ```
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class DependsOn(
    val dependencies: Array<String> = [],
)
