package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration

fun KSClassDeclaration.getConstructorParameterNames() =
    primaryConstructor?.parameters?.mapNotNull { it.name?.asString() } ?: listOf()
