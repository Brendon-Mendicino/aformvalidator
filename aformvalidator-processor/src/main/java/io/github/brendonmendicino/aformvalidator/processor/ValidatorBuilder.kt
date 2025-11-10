package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.NOTHING
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName

class ValidatorBuilder(
    val className: ClassName,
    val constructorProperties: List<Property>,
    val bodyProperties: List<Property>,
) {
    val allProperties: List<Property>
        get() = constructorProperties + bodyProperties

    val validatorClass = ClassName(className.packageName, className.simpleName + "Validator")

    companion object {
        fun from(clazz: KSClassDeclaration): ValidatorBuilder {
            val constructorParameters = clazz.getConstructorParameterNames().toSet()
            val (constructorProperties, bodyProperties) = clazz
                .getAllProperties()
                .map { prop -> Property.from(prop) }
                .partition { prop -> prop.name in constructorParameters }

            return ValidatorBuilder(
                className = clazz.toClassName(),
                constructorProperties = constructorProperties,
                bodyProperties = bodyProperties,
            )
        }
    }

    fun buildConstructor(): Pair<FunSpec, List<PropertySpec>> {
        val properties = mutableListOf<PropertySpec>()
        val constructor = FunSpec.constructorBuilder()

        for (prop in constructorProperties.map { it.toParamState() }) {
            constructor.addParameter(prop.toParameterSpec())
            properties.add(prop.toPropertySpec(initializer = true))
        }

        return constructor.build() to properties.toList()
    }

    fun bodyPropertySpecs(): List<PropertySpec> {
        // Convert the body properties into ParamState and
        // assign an initializer
        val paramStateProperties = bodyProperties.map { it.toParamState() }
            .map { it.toBodyProperty(CodeBlock.of("this._inner.${it.property.name}")) }

        val inner = PropertySpec.builder("_inner", className, KModifier.PRIVATE)
            .initializer("this.toData()")
            .build()

        val isError = PropertySpec.builder("isError", Boolean::class, KModifier.PUBLIC)
            .initializer(
                CodeBlock.of(allProperties.joinToString(" || ") { "this.${it.name}.isError" })
            )
            .addKdoc("True if any of the fields has an error")
            .build()

        val used = PropertySpec.builder("used", Boolean::class, KModifier.PUBLIC)
            .initializer(
                CodeBlock.of(allProperties.joinToString(" || ") { "this.${it.name}.used" })
            )
            .addKdoc("True if any of the fields is used")
            .build()

        val allUsed = PropertySpec.builder("allUsed", Boolean::class, KModifier.PUBLIC)
            .initializer(
                CodeBlock.of(allProperties.joinToString(" && ") { "this.${it.name}.used" })
            )
            .addKdoc("True if all the fields are used")
            .build()

        val errorType = allProperties
            .map { it.errorType }.filter { it != NOTHING }.distinct()
            .let {
                if (it.size > 1) ANY
                else it.firstOrNull() ?: NOTHING
            }
            .copy(nullable = true)

        val sequenceType = Sequence::class.asTypeName()
            .parameterizedBy(
                Pair::class.asTypeName().parameterizedBy(STRING, errorType.copy(nullable = false))
            )

        // Sequence of all the errors
        val errors = PropertySpec.builder("errors", sequenceType, KModifier.PUBLIC)
            .initializer(
                CodeBlock.builder()
                    .add(
                        "%M(%L)",
                        MemberName("kotlin.sequences", "sequenceOf"),
                        buildCodeBlock {
                            for (prop in allProperties) {
                                add("Pair(%S, this.${prop.name}.error), ", prop.name)
                            }
                        },
                    )
                    .add(
                        ".%M { (name, error) -> error?.let { Pair(name, error) } }",
                        MemberName("kotlin.sequences", "mapNotNull")
                    )
                    .build()
            )
            .addKdoc("Sequence of all the errors in the validator")
            .build()

        val error = PropertySpec.builder("error", errorType, KModifier.PUBLIC)
            .initializer(
                CodeBlock.builder()
                    .addStatement(
                        "this.errors.%M { it.second }.%M()",
                        MemberName("kotlin.sequences", "map"),
                        MemberName("kotlin.sequences", "firstOrNull"),
                    )
                    .build()
            )
            .addKdoc("Get any of the field errors, if no error is present this field is `null`")
            .build()

        return listOf(inner) + paramStateProperties + isError + used + allUsed + errors + error

    }

    fun toDataSpec(): FunSpec {
        val constructorInitializer = constructorProperties
            .joinToString(separator = ",\n") { "${it.name} = this.${it.name}.value" }

        return FunSpec.builder("toData")
            .returns(className)
            .addStatement("return %T($constructorInitializer)", className)
            .build()
    }

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
    fun buildUpdater(): TypeSpec {
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
                    "${update.name} = if (%N) validator.%L.update(%N) else validator.${update.name}, ",
                    isSet,
                    update.name,
                    update,
                )
            }

        return TypeSpec.classBuilder("Updater")
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
                    .addCode("return %T(", validatorClass)
                    .apply {
                        for (code in validatorBuilder) {
                            addCode(code)
                        }
                    }
                    .addCode(")")
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
    fun updateFunc(updater: TypeName): FunSpec {
        return FunSpec.builder("update")
            .addModifiers(KModifier.PUBLIC)
            .returns(validatorClass)
            .addParameter("transform", LambdaTypeName.get(receiver = updater, returnType = UNIT))
            .addStatement("val updater = %T(this)", updater)
            .addStatement("updater.transform()")
            .addStatement("return updater.build()")
            .build()
    }

    fun buildValidator(): TypeSpec {
        val (constructor, constructorProperties) = buildConstructor()
        val bodyProperties = bodyPropertySpecs()
        val toData = toDataSpec()
        val updater = buildUpdater()
        val update = updateFunc(validatorClass.nestedClass(updater.name!!))

        return TypeSpec.classBuilder(className.simpleName + "Validator")
            .addModifiers(KModifier.DATA)
            .primaryConstructor(constructor)
            .addProperties(constructorProperties)
            .addProperties(bodyProperties)
            .addFunction(toData)
            .addType(updater)
            .addFunction(update)
            .build()
    }

    /**
     * fun State.toValidator(): Validator {
     *      return Validator(
     *          prop = ParamState<T, ERROR>(
     *              value = this.prop,
     *              conditions = listOf<ValidatorCond<T, ERROR>().flatMap { it.conditions }
     *          ),
     *          // ...
     *      )
     * }
     */
    fun converterCode(): CodeBlock {
        val paramStateInit = constructorProperties.map { it.toParamState() }
            .map {
                it.toInitializer(
                    CodeBlock.builder()
                        .add("this.${it.property.name}")
                        .build()
                )
            }

        return CodeBlock.builder()
            .add("return %T(", validatorClass)
            .apply {
                for ((stateInit, prop) in paramStateInit.zip(constructorProperties)) {
                    add("${prop.name} = ")
                    add(stateInit)
                    add(", ")
                }
            }
            .add(")")
            .build()
    }

    /**
     * Build the toValidator() converter
     */
    fun buildConverter(): FunSpec {
        return FunSpec.builder("toValidator")
            .receiver(className)
            .returns(validatorClass)
            .addCode(converterCode())
            .build()
    }

    /**
     * Generate the file with the generated Validator and toValidator()
     */
    fun build(): FileSpec {
        return FileSpec.builder(className.packageName, validatorClass.simpleName + ".kt")
            .addType(buildValidator())
            .addFunction(buildConverter())
            .build()
    }
}