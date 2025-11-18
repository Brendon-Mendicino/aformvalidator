package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.NOTHING
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName

class Property(
    val name: String,
    val type: TypeName,
    val errorType: TypeName,
    val errorKSType: KSType?,
    val metadata: List<KSType?>,
    val validators: List<TypeName>,
    val validatorImpls: List<AnnotationSpec>,
    val modifiers: List<KModifier>,
    val dependencies: Set<String>,
    val kdoc: String?,
) {
    companion object {
        /**
         * @throws IllegalArgumentException
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

            val (validators, errorTypes) = propertyAnnotations
                .flatMap { annotation -> annotation.validator.validatorClasses() }
                .map { (validatorRef, errorRef) -> validatorRef.toClassName() to errorRef }
                .unzip()

            // Retrieve the metadata from the validator implementations
            val metadata = propertyAnnotations
                .map {
                    it.impl.argumentByName("metadata")
                        ?: throw IllegalArgumentException("Any annotation annotated with @Validator must implement a 'val metadata: KClass<out Metadata>' property!")
                }
                .map {
                    it.value as? KSType
                        ?: throw IllegalArgumentException("Any annotation annotated with @Validator must implement a 'val metadata: KClass<out Metadata>' property! The current type is not a `KClass`!")
                }
                .map { metadataType -> metadataType.takeIf { it.toClassName() != NOTHING } }
                // IMPORTANT: apparently Nothing gets converted to Void is it's in a different project, strange...
                .map { metadataType -> metadataType?.takeIf { (it.declaration as? KSClassDeclaration)?.qualifiedName?.asString() != Void::class.qualifiedName!! } }

            // Find the most common ancestor among the error types
            val errorKSType = errorTypes
                .filter { type -> type.declaration is KSClassDeclaration }
                .reduceOrNull { l, r -> l.commonAncestor(r) }

            val errorType = errorKSType?.toTypeName() ?: NOTHING

            return Property(
                name = propertyName,
                type = propertyType,
                errorType = errorType,
                errorKSType = errorKSType,
                metadata = metadata,
                validators = validators,
                validatorImpls = propertyAnnotations.map { it.impl.toAnnotationSpec() },
                modifiers = modifiers,
                dependencies = dependencies,
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
        return "Property(name=$name, type=$type, errorType=$errorType, validators=$validators)"
    }

    private fun metadataInstantiation(metadata: KSType?): CodeBlock {
        if (metadata == null) {
            return CodeBlock.of("null")
        }

        require(metadata.declaration is KSClassDeclaration) {
            "Metadata type declaration must a KSClassDeclaration! declaration=${metadata.declaration}"
        }

        val isObject = (metadata.declaration as KSClassDeclaration).classKind == ClassKind.OBJECT

        return if (isObject) {
            CodeBlock.of("%T", metadata.toTypeName())
        } else {
            CodeBlock.of("%T()", metadata.toTypeName())
        }
    }

    fun metadataInstantiations(): List<CodeBlock> = metadata.map { metadataInstantiation(it) }
}