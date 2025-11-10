package io.github.brendonmendicino.aformvalidator.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import io.github.brendonmendicino.aformvalidator.annotation.ParamState
import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond

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
        val used = CodeBlock.of(property.dependencies.joinToString(" || ") { "this.$it.used" })

        val propertySpec = toPropertySpec()
        val initializer = toInitializer(value, used)

        return propertySpec.toBuilder()
            .initializer(initializer)
            .build()
    }

    fun toInitializer(value: CodeBlock, used: CodeBlock? = null): CodeBlock {
        val condType = ValidatorCond::class.asTypeName()
            .parameterizedBy(property.type, property.errorType)

        return CodeBlock.builder()
            .add("%T(", paramStateType)
            .add("value = ")
            .add(value)
            .add(", ")
            .add("conditions = listOf<%T>(", condType)
            .apply {
                for ((validator, impl) in property.validators.zip(property.validatorImpls)) {
                    add("%T(", validator)
                    for (member in impl.members) {
                        add(member)
                        add(", ")
                    }
                    add(")")
                }
            }
            .add(").%M { it.conditions }, ", MemberName("kotlin.collections", "flatMap"))
            .apply {
                if (used != null) {
                    add("used = ")
                    add(used)
                    add(", ")
                }
            }
            .add(")")
            .build()
    }

    override fun toString(): String {
        return "PramState(property=$property)"
    }
}