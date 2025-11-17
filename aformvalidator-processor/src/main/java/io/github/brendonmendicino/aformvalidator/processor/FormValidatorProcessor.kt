package io.github.brendonmendicino.aformvalidator.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.writeTo
import io.github.brendonmendicino.aformvalidator.annotation.annotations.FormState

lateinit var globalLogger: KSPLogger

class FormValidatorProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    init {
        globalLogger = logger
    }

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
        logger.info("[aformvalidator] processing ${classDeclaration.qualifiedName?.asString()}", classDeclaration)
        try {
            ValidatorBuilder.from(classDeclaration)
                .build()
                .writeTo(codeGenerator, aggregating = true)
        } catch (e: Throwable) {
            logger.error(
                "[aformvalidator] An error occurred while processing ${classDeclaration.qualifiedName?.asString()}",
                classDeclaration
            )
            throw e
        }
    }
}