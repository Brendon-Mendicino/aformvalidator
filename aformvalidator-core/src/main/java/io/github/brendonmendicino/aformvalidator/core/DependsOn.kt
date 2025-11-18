package io.github.brendonmendicino.aformvalidator.core

/**
 * Mark that a property depends on other properties of the class.
 *
 * Used to correctly updated the [ParamState.used] parameter.
 *
 * # Examples
 *
 * When a form has a [Number] field, but it must be represented
 * as a [String] and a direct conversion to the integer is
 * wanted, a dependable property can be used. By implicitly
 * overriding the setter method for a property, that depend
 * on other properties, the validity state can be tracked
 * also for the [Int] property.
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
//    @Language("kotlin")
    val dependencies: Array<String> = [],
)