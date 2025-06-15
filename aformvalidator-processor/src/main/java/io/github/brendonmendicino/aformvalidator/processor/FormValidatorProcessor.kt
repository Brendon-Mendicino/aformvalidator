package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.ParamState
import io.github.brendonmendicino.aformvalidator.annotation.Validator
import io.github.brendonmendicino.aformvalidator.annotation.ValidatorCond
import kotlin.reflect.KClass

class FormValidatorProcessor(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(FormState::class.qualifiedName!!)

        symbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.CLASS }
            .forEach { classDeclaration ->
                generateValidatorClass(classDeclaration)
            }

        return emptyList()
    }

    private fun generateValidatorClass(classDeclaration: KSClassDeclaration) {
        val className = classDeclaration.simpleName.asString()
        val packageName = classDeclaration.packageName.asString()
        val validatorClass = "${className}Validator"
        val extensionClass = "${className}Extension"

        val validatorClassFile = codeGenerator.createNewFile(
            dependencies = Dependencies(true, classDeclaration.containingFile!!),
            packageName = packageName,
            fileName = validatorClass,
        )

        validatorClassFile.writer().use { writer ->
            val constructorProperties = classDeclaration.getValidatorClassConstructor()
            val toDataConstructorProperties = classDeclaration.getToDataConstructorProperties()
            val isError = classDeclaration.getValidatorClassIsError()
            val errors = classDeclaration.getValidatorClassErrors()
            val oneUsed = classDeclaration.getValidatorClassOneUsed()
            val allUsed = classDeclaration.getValidatorClassAllUsed()


            writer.write(
                """
@file:Suppress("warnings")

package $packageName
    
data class $validatorClass(
    $constructorProperties
) {
    $isError
    
    $errors
    
    $oneUsed
    
    $allUsed

    fun toData(): $className {
        return $className(
            $toDataConstructorProperties
        )
    }
}
""".trimIndent()
            )
        }

        val extensionFile = codeGenerator.createNewFile(
            dependencies = Dependencies(true, classDeclaration.containingFile!!),
            packageName = packageName,
            fileName = extensionClass,
        )

        extensionFile.writer().use { writer ->
            classDeclaration.checkAllAnnotationErrorType()
            val properties =
                classDeclaration.getAllProperties().map { it.getValidatorInstantiationProperty() }

            writer.write(
                """
@file:Suppress("warnings")

package $packageName

fun $className.toValidator(): $validatorClass {
    return $validatorClass(
        ${properties.joinToString()}
    )
}
""".trimIndent()
            )
        }
    }

    /**
     * Get the Validator class properties when calling the constructor.
     */
    fun KSClassDeclaration.getValidatorClassConstructor(): String {
        val properties =
            this.getAllProperties().map { it.getValidatorConstructorProperty() }

        val propertiesToString = properties.joinToString(postfix = ",")

        return propertiesToString
    }

    fun KSClassDeclaration.getValidatorClassIsError(): String {
        val propertyNames = getAllProperties().map { it.simpleName.asString() }
        val isError = propertyNames.map { "$it.error != null" }.joinToString(separator = " || ")

        return "val isError: Boolean = $isError"
    }

    fun KSClassDeclaration.getValidatorClassErrors(): String {
        val propertyNames = getAllProperties().map { it.simpleName.asString() }
        val errors = propertyNames.map { "$it.error" }.joinToString()

        return "val errors = kotlin.sequences.sequenceOf($errors).filterNotNull()"
    }

    fun KSClassDeclaration.getValidatorClassOneUsed(): String {
        val propertyNames = getAllProperties().map { it.simpleName.asString() }
        val oneUsed = propertyNames.map { "$it.used" }.joinToString(separator = " || ")

        return "val oneUsed = $oneUsed"
    }

    fun KSClassDeclaration.getValidatorClassAllUsed(): String {
        val propertyNames = getAllProperties().map { it.simpleName.asString() }
        val allUsed = propertyNames.map { "$it.used" }.joinToString(separator = " && ")

        return "val allUsed = $allUsed"
    }

    /**
     * The the Data class properties when the calling the Data constructor
     * in the `toData()` function call.
     *
     * The `.value` attribute comes from the [ParamState]
     */
    fun KSClassDeclaration.getToDataConstructorProperties(): String {
        val propertyNames =
            this.getAllProperties().map { it.simpleName.asString() }

        val toDataPropertyAssignments =
            propertyNames.map { "$it = this.$it.value" }.joinToString(postfix = ",")

        return toDataPropertyAssignments
    }

    fun KSPropertyDeclaration.getValidatorConstructorProperty(): String {
        val propertyName = simpleName.asString()
        val propertyType = type.resolve().getType()!!
        val errorType = annotations
            .getHierarchy()
            .filterAnnotationsByType(Validator::class)
            .map { it.getArgumentType(Validator<*>::errorType.name)!! }
            .map { it.getType()!! }
            // If there is no errorType the field has not been annotated.
            .firstOrNull()
            ?: "Nothing"

        return "val $propertyName: ${ParamState::class.qualifiedName!!}<$propertyType, $errorType>"
    }

    fun KSClassDeclaration.checkAllAnnotationErrorType() {
        val errorTypeSequence = this
            .getAllProperties()
            .flatMap { it.annotations }
            .getHierarchy()
            .filterAnnotationsByType(Validator::class)
            .map { it.getArgumentType(Validator<*>::errorType.name)!! }
            .map { it.getType()!! }
            .iterator()

        val errorType = errorTypeSequence.next()

        errorTypeSequence.forEach {
            if (errorType != it)
            // TODO: improve this logging message
                throw Exception("All ${Validator::class.simpleName!!}.${Validator<*>::errorType.name} of class ${this.qualifiedName!!.asString()} must have the same type: $errorType, one type was: $it")
        }
    }

    fun KSPropertyDeclaration.getValidatorInstantiationProperty(): String {
        val propertyName = simpleName.asString()
        val propertyType = type.resolve().getType()!!
        type.resolve().isMarkedNullable
        val errorType = annotations
            .getHierarchy()
            .filterAnnotationsByType(Validator::class)
            .map { it.getArgumentType(Validator<*>::errorType.name)!! }
            .map { it.getType()!! }
            .firstOrNull()
            ?: "Nothing"

        // Check that all validators have the same return type
        annotations
            .getHierarchy()
            .filterAnnotationsByType(Validator::class)
            .map { it.getArgumentType(Validator<*>::errorType.name)!! }
            .forEach {
                if (errorType != it.getType())
                    throw Exception("Ma io che cazzo ne so scusa")
            }

        // Now the validator constructor is called with arguments,
        // this is obtained by taking validator annotation and it's
        // parent, and calling the validator arguments with the
        // parent annotation values.
        val validatorClasses = annotations
            .getHierarchy()
            .flatMap { it.getValidatorAndParent() }
            .map { (validator, parent) -> getValidatorConstructor(validator, parent) }

        return """
        $propertyName = ${ParamState::class.qualifiedName!!}<$propertyType, $errorType>(
            value = this.$propertyName,
            conditions = 
                // List of validator classes
                listOf<${ValidatorCond::class.qualifiedName!!}<$propertyType, $errorType>>(
                    ${validatorClasses.joinToString()}
                ).flatMap { it.conditions },
            used = false,
        )
        """.trimIndent()
    }

    fun <T : Annotation> Sequence<KSAnnotation>.filterAnnotationsByType(annotation: KClass<T>) =
        filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == annotation.qualifiedName }

    fun KSAnnotation.getArgumentType(argName: String): KSType? {
        val argument = arguments.firstOrNull { it.name?.asString() == argName }

        return argument?.value as? KSType
    }

    fun KSAnnotation.getValidatorAndParent(): Sequence<Pair<KSAnnotation, KSAnnotation>> =
        annotationType
            .resolve()
            .declaration
            .annotations
            .filterAnnotationsByType(Validator::class)
            .map { Pair(it, this) }

    fun getValidatorConstructor(validator: KSAnnotation, parent: KSAnnotation): String {
        val validatorType = validator.getArgumentType(Validator<*>::value.name)!!
        val validatorParameters =
            (validatorType.declaration as KSClassDeclaration).primaryConstructor!!.parameters.map { it.name!!.asString() }

        val parentParameterValues = parent.arguments.associateBy { it.name!!.asString() }

        fun Any?.toParam(): String = when (this) {
            is String -> "\"\"\"${this.replace("$", "\${'$'}")}\"\"\""
            else -> this.toString()
        }

        val constructorParameters =
            validatorParameters.joinToString { "$it = ${parentParameterValues[it]!!.value.toParam()}" }

        return "${validatorType.getType()!!}($constructorParameters)"
    }

    fun Sequence<KSAnnotation>.getHierarchy(): Sequence<KSAnnotation> =
        flatMap { it.getHierarchy() }

    fun buildHierarchy(annotation: KSAnnotation, seenAnnotations: Set<String>): List<KSAnnotation> {
        val seenAnnotations = seenAnnotations.toMutableSet()

        seenAnnotations.add(annotation.shortName.asString())

        return annotation
            .annotationType
            .resolve()
            .declaration
            .annotations
            .filter { it.shortName.asString() !in seenAnnotations }
            .toList()
            .flatMap { buildHierarchy(it, seenAnnotations) }
            .plus(annotation)
    }

    fun KSAnnotation.getHierarchy(): List<KSAnnotation> = buildHierarchy(this, setOf())

    fun KSType.getType(): String? {
        val baseType = declaration.qualifiedName?.asString()
        val nullable = if (isMarkedNullable) "?" else ""
        val children = arguments.mapNotNull { it.type?.resolve()?.getType() }

        return if (children.isEmpty()) {
            "$baseType$nullable"
        } else {
            children.joinToString(prefix = "$baseType<", postfix = ">$nullable")
        }
    }
}