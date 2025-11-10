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
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toClassNameOrNull
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.brendonmendicino.aformvalidator.annotation.Validator

class Property(
    val name: String,
    val type: TypeName,
    val errorType: TypeName,
    val validators: List<TypeName>,
    val annotations: List<PropertyValidator>,
    val modifiers: List<KModifier>,
    val kdoc: String?,
) {
    data class PropertyValidator(
        val validator: AnnotationSpec,
        val impl: AnnotationSpec,
    )

    companion object {
        /**
         * @throws IllegalStateException when [Validator] annotations have different [Validator.errorType]
         */
        fun from(property: KSPropertyDeclaration): Property {
            val propertyName = property.simpleName.asString()
            val propertyType = property.type.toTypeName()
            val modifiers = property.modifiers.mapNotNull { it.toKModifier() }
            val propertyAnnotations = property
                .annotations
                .flatMap { annotation -> annotation.validators() }
                .toList()

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

//            decl.toClassName().withTypeArguments(arguments.map { it.toTypeName(typeParamResolver) })

            val cn = propertyAnnotations.map { it.validator }.firstOrNull()?.annotationType?.resolve()
            val an = cn?.arguments
            globalLogger.warn("$propertyName $propertyAnnotations ${propertyAnnotations.map { it.validator.annotationType.resolve() }} $cn ${cn?.toClassNameOrNull()} $an", property)

            return Property(
                name = propertyName,
                type = propertyType,
                errorType = errorType,
                validators = validators,
                annotations = propertyAnnotations.map {
                    PropertyValidator(
                        it.validator.toAnnotationSpec(),
                        it.impl.toAnnotationSpec()
                    )
                },
                modifiers = modifiers,
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
        return "Property(name=$name, type=$type, errorType=$errorType, annotations=$annotations"
    }
}