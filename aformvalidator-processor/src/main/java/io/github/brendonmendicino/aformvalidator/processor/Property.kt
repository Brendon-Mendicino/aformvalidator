package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.NOTHING
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.brendonmendicino.aformvalidator.annotation.ToValidator
import io.github.brendonmendicino.aformvalidator.annotation.Validator

class Property(
    val name: String,
    val type: TypeName,
    val errorType: TypeName,
    val validators: List<TypeName>,
    val validatorImpls: List<AnnotationSpec>,
    val modifiers: List<KModifier>,
    val dependencies: Set<String>,
    val toValidator: Boolean,
    val kdoc: String?,
) {
    companion object {
        /**
         * @throws IllegalStateException when [Validator] annotations have different [Validator.errorType]
         */
        fun from(property: KSPropertyDeclaration): Property {
            globalLogger.info(
                "[aformvalidator] processing property ${property.simpleName.asString()}",
                property
            )
            val propertyName = property.simpleName.asString()
            val propertyType = property.type.toTypeName()
            val modifiers = property.modifiers.mapNotNull { it.toKModifier() }

            val propertyAnnotations = property
                .annotations
                .flatMap { annotation -> annotation.validators() }
                .toList()

            val dependencies = property
                .annotations
                .flatMap { annotation -> annotation.dependencies() }
                .toSet()

            val errorTypes = propertyAnnotations
                .map { annotation -> annotation.validator }
                // Get the errorType argument of every annotation
                .map { annotation -> annotation.arguments.first { arg -> arg.name?.asString() == Validator<*>::errorType.name } }
                .map { argument -> (argument.value as KSType).toTypeName() }
                .distinct()

            if (errorTypes.size > 1) {
                throw IllegalStateException("property=\"$propertyName\" is annotated with different @Validators errorType. All errorTypes must equals! Types found: $errorTypes")
            }

            val errorType = errorTypes.firstOrNull() ?: NOTHING

            val validators = propertyAnnotations
                .map { annotation -> annotation.validator }
                .map { annotation -> annotation.arguments.first { arg -> arg.name?.asString() == Validator<*>::value.name } }
                .map { argument -> (argument.value as KSType).toTypeName() }

            val toValidator = property.annotations.any { annotation ->
                annotation.isClass(
                    ToValidator::class
                )
            }

            return Property(
                name = propertyName,
                type = propertyType,
                errorType = errorType,
                validators = validators,
                validatorImpls = propertyAnnotations.map { it.impl.toAnnotationSpec() },
                modifiers = modifiers,
                dependencies = dependencies,
                toValidator = toValidator,
                kdoc = property.docString,
            )
        }
    }

    fun toParamState() = PropertyParamState(
        property = this
    )

    fun toParameterSpec(): ParameterSpec {
        return ParameterSpec.builder(name, type, modifiers)
            .build()
    }

    /**
     * @param initializer This is needed when building a class constructor.
     */
    fun toPropertySpec(initializer: Boolean = false): PropertySpec {
        val builder = PropertySpec.builder(name, type, modifiers)

        if (initializer)
            builder.initializer(name)

        if (kdoc != null)
            builder.addKdoc(kdoc)

        return builder.build()
    }

    override fun toString(): String {
        return "Property(name=$name, type=$type, errorType=$errorType, annotations"
    }
}