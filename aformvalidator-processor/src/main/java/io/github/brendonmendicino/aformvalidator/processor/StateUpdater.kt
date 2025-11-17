package io.github.brendonmendicino.aformvalidator.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.joinToCode

class StateUpdater(
    val validatorClass: ClassName,
    val constructorProperties: List<Property>,
) {
    val updaterClass = validatorClass.nestedClass("Updater")

    /**
     * ```
     * class Validator(/* ... */) {
     *      class Updater(private val validator: Validator) {
     *          var propIsSet: Boolean = false
     *          var prop: T = validator.prop.value
     *              set(value) {
     *                  propIsSet = true
     *                  field = value
     *              }
     *
     *          // ...
     *
     *          private fun build(): Validator {
     *              return Validator(
     *                  prop = if (propIsSet) validator.prop.update(prop) else validator.prop,
     *                  // ...
     *             )
     *          }
     *      }
     * }
     * ```
     */
    fun updater(): TypeSpec {
        val (setConditions, updaters) = constructorProperties
            .map { property ->
                val isSet =
                    PropertySpec.builder("${property.name}IsSet", Boolean::class, KModifier.PRIVATE)
                        .mutable()
                        .initializer("false")
                        .build()

                val updater = property.toPropertySpec().toBuilder()
                    .mutable()
                    .initializer("validator.${property.name}.value")
                    .setter(
                        FunSpec.setterBuilder()
                            .addParameter(property.toParameterSpec())
                            .addStatement("%N = true", isSet)
                            .addStatement("field = %N", property.name)
                            .build()
                    )
                    .build()

                isSet to updater
            }
            .unzip()

        val validatorBuilder = setConditions.zip(updaters)
            .map { (isSet, update) ->
                CodeBlock.of(
                    "${update.name} = if (%N) validator.%L.update(%N) else validator.${update.name}",
                    isSet,
                    update.name,
                    update,
                )
            }

        return TypeSpec.classBuilder(updaterClass)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder(
                            "validator",
                            validatorClass,
                        ).build()
                    ).build()
            )
            .addProperty(
                PropertySpec.builder("validator", validatorClass, KModifier.PRIVATE)
                    .initializer("validator")
                    .build()
            )
            .addProperties(setConditions)
            .addProperties(updaters)
            .addFunction(
                FunSpec.builder("build")
                    .addModifiers(KModifier.INTERNAL)
                    .returns(validatorClass)
                    .addCode("return %T(%L)", validatorClass, validatorBuilder.joinToCode(", "))
                    .build()
            )
            .build()
    }

    /**
     * ```
     * fun update(transform: Updater.() -> Unit): Validator {
     *      val updater = Updater(this)
     *      updater.transform()
     *      return updater.build()
     * }
     * ```
     */
    fun updateFunc(): FunSpec {
        return FunSpec.builder("update")
            .addModifiers(KModifier.PUBLIC)
            .returns(validatorClass)
            .addParameter("transform", LambdaTypeName.get(receiver = updaterClass, returnType = UNIT))
            .addStatement("val updater = %T(this)", updaterClass)
            .addStatement("updater.transform()")
            .addStatement("return updater.build()")
            .build()
    }
}