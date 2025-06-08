package lol.terabrendon.aformvalidator.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAnnotationsByType
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
import lol.terabrendon.aformvalidator.annotation.FormState
import lol.terabrendon.aformvalidator.annotation.ParamState
import lol.terabrendon.aformvalidator.annotation.Validator
import lol.terabrendon.aformvalidator.annotation.ValidatorCond
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

            writer.write(
                """
package $packageName
    
data class $validatorClass(
    $constructorProperties
) {
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
        val errorType = try {
            val errorType = annotations
                .getHierarchy()
                .filterAnnotationsByType(Validator::class)
                .map { it.getArgumentType(Validator<*>::errorType.name)!! }
                .map { it.getType()!! }
                .first()
            errorType
        } catch (_: Exception) {
            throw Exception(annotations.getHierarchy().toList().toString())
//            throw Exception(annotations.map { it.annotationType.resolve().annotations.toList().toString() }.toList().toString())
        }

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
            .first()

        // Check that all validators have the same return type
        annotations
            .getHierarchy()
            .filterAnnotationsByType(Validator::class)
            .map { it.getArgumentType(Validator<*>::errorType.name)!! }
            .forEach {
                if (errorType != it.getType())
                    throw Exception("Ma io che cazzo ne so scusa")
            }

        val validatorClasses = annotations
            .getHierarchy()
            .filterAnnotationsByType(Validator::class)
            .map { it.getArgumentType(Validator<*>::value.name)!! }
            // Get the type
            .map { it.getType()!! }
            // Call the constructor
            .map { "$it()" }

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

    @OptIn(KspExperimental::class)
    fun KSPropertyDeclaration.getPropertyDependencies(): List<String> {
        val validatorAnnotations = getAnnotationsByType(Validator::class)
        val errorType = validatorAnnotations.map { it.errorType }.first()

        return setOf<String>(
            type.containingFile?.packageName?.toString()!!,
            errorType.qualifiedName!!,
            ParamState::class.qualifiedName!!,
            ValidatorCond::class.qualifiedName!!,
        ).plus(
            validatorAnnotations.map { it.value.qualifiedName!! },
        ).toList()
    }

    fun <T : Annotation> Sequence<KSAnnotation>.filterAnnotationsByType(annotation: KClass<T>) =
        filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == annotation.qualifiedName }

    fun KSAnnotation.getArgumentType(argName: String): KSType? {
        val argument = arguments.firstOrNull { it.name?.asString() == argName }

        return argument?.value as? KSType

    }

    fun Sequence<KSAnnotation>.getHierarchy(): Sequence<KSAnnotation> =
        plus(flatMap {
            it.annotationType.resolve().declaration.annotations.toList()
        })

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