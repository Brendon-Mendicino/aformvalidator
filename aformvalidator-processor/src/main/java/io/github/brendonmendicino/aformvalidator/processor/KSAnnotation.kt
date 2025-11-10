package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import io.github.brendonmendicino.aformvalidator.annotation.DependsOn
import io.github.brendonmendicino.aformvalidator.annotation.Validator

private val LEAFS = listOf(
    Target::class,
    Retention::class,
    Repeatable::class,
    MustBeDocumented::class,
).map { it.qualifiedName!! }

val VALIDATOR = Validator::class.asTypeName()

data class ValAnnotation(
    val validator: KSAnnotation,
    val impl: KSAnnotation,
)

fun KSAnnotation.validators(depth: Int = 0): Sequence<ValAnnotation> {
    if (depth == 100) {
        throw RuntimeException("ERROR: the function for retrieving the @Validator annotations failed! A loop was encountered in the annotation graph!")
    }

    val resolved = this.annotationType.resolve()

    if (resolved.declaration.qualifiedName?.asString() in LEAFS) return sequenceOf()

    // Check if any of the annotations are of @Validator type
    val validator = resolved.declaration.annotations
        .firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == Validator::class.qualifiedName!! }

    val returnSequence = if (validator != null) sequenceOf(ValAnnotation(validator, this))
    else sequenceOf()

    return returnSequence + resolved.declaration.annotations.flatMap { annotation ->
        annotation.validators(
            depth + 1
        )
    }
}

fun KSAnnotation.dependencies(): List<String> {
    val spec = this.toAnnotationSpec()
    if (spec.typeName != DependsOn::class.asTypeName())
        return emptyList()

    val dep = this.arguments.first { it.name?.asString() == DependsOn::dependencies.name }

    return (dep.value as ArrayList<*>).map { it as String }
}