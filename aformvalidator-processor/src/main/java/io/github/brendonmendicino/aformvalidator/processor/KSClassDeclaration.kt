package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType

fun KSClassDeclaration.getConstructorParameterNames() =
    primaryConstructor?.parameters?.mapNotNull { it.name?.asString() } ?: listOf()

fun KSType.parents(): List<KSType> = (declaration as KSClassDeclaration)
    .superTypes
    .map { it.resolve() }
    .filter { it.declaration is KSClassDeclaration }
    .toList()

fun KSType.ancestors(): Sequence<KSType> {
    val type = this
    require(declaration is KSClassDeclaration) {
        "The current KSType is not a KSClassDeclaration! declaration=${this.declaration}"
    }

    return sequence {
        val seen = mutableSetOf<KSType>()
        val queue = ArrayDeque(listOf(type))

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            yield(current)

            if (seen.add(current)) {
                current.parents().forEach { queue += it }
            }
        }
    }
}

fun KSType.commonAncestor(other: KSType): KSType {
    require(declaration is KSClassDeclaration) {
        "The current KSType is not a KSClassDeclaration! declaration=${this.declaration}"
    }

    val ancestors = ancestors().map { it.starProjection() }.toList()

    for (ancestor in other.ancestors().map { it.starProjection() }) {
        if (ancestor in ancestors) {
            return ancestor
        }
    }

    throw RuntimeException("No common ancestor between $this and $other. Any must a common ancestor!")
}