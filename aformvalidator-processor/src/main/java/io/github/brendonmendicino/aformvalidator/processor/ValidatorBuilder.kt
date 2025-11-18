package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.NOTHING
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class ValidatorBuilder(
    val className: ClassName,
    val constructorProperties: List<Property>,
    val bodyProperties: List<Property>,
) {
    val allProperties: List<Property>
        get() = constructorProperties + bodyProperties

    val validatorClass = ClassName(className.packageName, className.simpleName + "Validator")

    init {
        val usableDeps = constructorProperties.map { it.name }.toMutableSet()

        // Validate dependencies
        for (bodyProp in bodyProperties) {
            for (dep in bodyProp.dependencies) {
                if (dep !in usableDeps) {
                    val msg =
                        "[aformvalidator] Error while processing ${className.canonicalName} property=${bodyProp.name}. The dependency property=$dep does not exists, or it's declared after \"${bodyProp.name}\" declaration! Change your @DependsOn signature."
                    globalLogger.error(msg)
                    throw IllegalArgumentException(msg)
                }
            }

            usableDeps.add(bodyProp.name)
        }
    }

    companion object {
        fun from(clazz: KSClassDeclaration): ValidatorBuilder {
            globalLogger.info(
                "[aformvalidator] creating ValidatorBuilder for ${clazz.qualifiedName?.asString()}",
                clazz
            )
            val constructorParameters = clazz.getConstructorParameterNames().toSet()
            val (constructorProperties, bodyProperties) = clazz
                .getAllProperties()
                .map { prop ->
                    try {
                        Property.from(prop)
                    } catch (e: IllegalArgumentException) {
                        globalLogger.error(
                            "[aformvalidator] error while processing property of ${clazz.qualifiedName?.asString()}",
                            prop
                        )
                        throw e
                    }
                }
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
            .addKdoc(
                """Used by bodyProperties to get their value.
                |The reason is that in ksp it's not possible to get the setter of property.""".trimMargin()
            )
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

        val errorType = allProperties.mapNotNull { it.errorKSType }
            .reduceOrNull { l, r -> l.commonAncestor(r) }
            ?.toTypeName()
            ?: NOTHING

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

        val error = PropertySpec.builder("error", errorType.copy(nullable = true), KModifier.PUBLIC)
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

        // If no bodyProperty is present there is no need for the _inner
        val innerToInsert = if (bodyProperties.isEmpty()) emptyList() else listOf(inner)

        return innerToInsert + paramStateProperties + isError + used + allUsed + errors + error

    }

    fun toDataSpec(): FunSpec {
        val constructorInitializer = constructorProperties
            .map { CodeBlock.of("${it.name} = this.${it.name}.value") }

        return FunSpec.builder("toData")
            .returns(className)
            .addStatement("return %T(%L)", className, constructorInitializer.joinToCode(", "))
            .build()
    }

    fun buildValidator(): TypeSpec {
        val updater = StateUpdater(validatorClass, constructorProperties)

        val (constructor, constructorProperties) = buildConstructor()
        val bodyProperties = bodyPropertySpecs()
        val toData = toDataSpec()

        return TypeSpec.classBuilder(className.simpleName + "Validator")
            .addModifiers(KModifier.DATA)
            .primaryConstructor(constructor)
            .addProperties(constructorProperties)
            .addProperties(bodyProperties)
            .addFunction(toData)
            .addType(updater.updater())
            .addFunction(updater.updateFunc())
            .build()
    }

    /**
     * fun State.toValidator(): Validator {
     *      return Validator(
     *          prop = ParamState<T, ERROR>(
     *              value = this.prop,
     *              conditions = listOf<ValidatorCond<T, ERROR>(),
     *          ),
     *          // ...
     *      )
     * }
     */
    fun converterCode(): CodeBlock {
        val paramStateInit = constructorProperties
            .map { it.toParamState() }
            .map { paramState ->
                paramState.toInitializer(value = CodeBlock.of("this.${paramState.property.name}"))
            }

        val paramStateConstructorCalls = paramStateInit.zip(constructorProperties)
            .map { (stateInit, prop) -> CodeBlock.of("${prop.name} = %L", stateInit) }

        return CodeBlock.builder()
            .add("return %T(%L)", validatorClass, paramStateConstructorCalls.joinToCode(", "))
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