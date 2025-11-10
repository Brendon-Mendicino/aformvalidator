package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ANY
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
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import io.github.brendonmendicino.aformvalidator.annotation.ParamState
import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond

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
        val paramStateProperties =
            bodyProperties.map { it.toParamState() }.map { it.toPropertySpec() }

        val isError = PropertySpec.builder("isError", Boolean::class, KModifier.PUBLIC)
            .initializer(
                CodeBlock.builder()
                    .add(allProperties.joinToString(" && ") { "this.${it.name}.isError" })
                    .build()
            )
            .build()

        val used = PropertySpec.builder("used", Boolean::class, KModifier.PUBLIC)
            .initializer(
                CodeBlock.builder()
                    .add(allProperties.joinToString(" || ") { "this.${it.name}.used" })
                    .build()
            )
            .build()

        val allUsed = PropertySpec.builder("allUsed", Boolean::class, KModifier.PUBLIC)
            .initializer(
                CodeBlock.builder()
                    .add(allProperties.joinToString(" && ") { "this.${it.name}.used" })
                    .build()
            )
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

        "mapNotNull { (name, error) -> error?.let { Pair(name, error) } }"
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
            .build()

        return paramStateProperties + isError + used + allUsed + errors + error

    }

    fun toDataSpec(): FunSpec {
        val constructorInitializer = constructorProperties
            .joinToString(separator = ",\n") { "${it.name} = this.${it.name}.value" }

        return FunSpec.builder("toData")
            .returns(className)
            .addStatement("return %T($constructorInitializer)", className)
            .build()
    }

    fun buildValidator(): TypeSpec {
        val (constructor, constructorProperties) = buildConstructor()
        val bodyProperties = bodyPropertySpecs()
        val toData = toDataSpec()

        return TypeSpec.classBuilder(className.simpleName + "Validator")
            .addModifiers(KModifier.DATA)
            .primaryConstructor(constructor)
            .addProperties(constructorProperties)
            .addProperties(bodyProperties)
            .addFunction(toData)
            .build()
    }

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

    fun buildConverter(): FunSpec {
        return FunSpec.builder("toValidator")
            .receiver(className)
            .returns(validatorClass)
            .addCode(converterCode())
            .build()
    }

    fun build(): FileSpec {
        return FileSpec.builder(className.packageName, validatorClass.simpleName + ".kt")
            .addType(buildValidator())
            .addFunction(buildConverter())
            .build()
    }
}