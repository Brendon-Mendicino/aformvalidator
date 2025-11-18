package io.github.brendonmendicino.aformvalidator.processor

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.joinToCode
import io.github.brendonmendicino.aformvalidator.core.ParamState
import io.github.brendonmendicino.aformvalidator.core.ValidatorCond

class PropertyParamState(
    val property: Property,
) {
    val paramStateType: TypeName
        get() = ParamState::class.asTypeName()
            .parameterizedBy(property.type, property.errorType)

    fun toParameterSpec(): ParameterSpec {
        return ParameterSpec.builder(property.name, paramStateType, property.modifiers)
            .build()
    }

    /**
     * @param initializer This is needed when building a class constructor.
     */
    fun toPropertySpec(initializer: Boolean = false): PropertySpec {
        val builder = PropertySpec.builder(property.name, paramStateType, property.modifiers)

        if (initializer)
            builder.initializer(property.name)

        if (property.kdoc != null)
            builder.addKdoc(property.kdoc)

        return builder.build()
    }

    fun toBodyProperty(value: CodeBlock): PropertySpec {
        val used = CodeBlock.of(property.dependencies.joinToString(" || ") { "this.$it.used" }
            .ifBlank { "false" })

        val propertySpec = toPropertySpec()
        val initializer = toInitializer(value, used)

        return propertySpec.toBuilder()
            .initializer(initializer)
            .build()
    }

    fun AnnotationSpec.toConstructorCall(): CodeBlock {
        val type = typeName
        val args = members.map {
            if (it.toString() == "metadata = java.lang.Void::class") CodeBlock.of("metadata = Nothing::class")
            else it
        }.joinToCode(", ")
        return CodeBlock.of("%T(%L)", type, args)
    }

    fun toInitializer(value: CodeBlock, used: CodeBlock? = null): CodeBlock {
        val condType = ValidatorCond::class.asTypeName()
            .parameterizedBy(property.type, STAR, property.errorType)

        val validatorList = mutableListOf<CodeBlock>()

        for (i in 0..<property.validators.size) {
            val validatorCondType = property.validators[i]
            val metadata = property.metadataInstantiations()[i]
            val annotation = property.validatorImpls[i].toConstructorCall()

            val code = CodeBlock.of(
                "%T(metadata = %L, annotation = %L)",
                validatorCondType,
                metadata,
                annotation,
            )

            validatorList += code
        }

        return CodeBlock.builder()
            .add("%T(", paramStateType)
            .add("value = %L, ", value)
            .add("conditions = listOf<%T>(%L), ", condType, validatorList.joinToCode(", "))
            .run {
                if (used != null)
                    add("used = %L, ", used)
                else
                    this
            }
            .add(")")
            .build()
    }

    override fun toString(): String {
        return "PramState(property=$property)"
    }
}