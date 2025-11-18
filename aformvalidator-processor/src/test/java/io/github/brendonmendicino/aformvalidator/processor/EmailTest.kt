@file:OptIn(ExperimentalCompilerApi::class)

package io.github.brendonmendicino.aformvalidator.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File


class EmailTest {
    internal fun File.listFilesRecursively(): List<File> {
        return (listFiles()
            ?: throw RuntimeException("listFiles() was null. File is not a directory or I/O error occured"))
            .flatMap { file ->
                if (file.isDirectory)
                    file.listFilesRecursively()
                else
                    listOf(file)
            }
    }

    val core =
        "../aformvalidator-core/src/main/java/io/github/brendonmendicino/aformvalidator/core"
    val coreFiles = File(core).listFilesRecursively()

    val root =
        "../aformvalidator-annotation/src/main/java/io/github/brendonmendicino/aformvalidator/annotation"
    val files: List<File> = File(root).listFilesRecursively() + coreFiles
    val annotationSources = files.map { SourceFile.fromPath(it) }

    val rootTest =
        "../quicktest/src/main/java/io/github/brendonmendicino/aformvalidator/annotation"
    val filesTest = File(rootTest).listFilesRecursively() + coreFiles
    val annotationsSourcesTest = filesTest.map { SourceFile.fromPath(it) }

    @Test
    fun `email accepts valid addresses`() {
        val source = SourceFile.kotlin(
            "PersonEmail.kt",
            """
            package io.github.brendonmendicino.aformvalidator.processor
            
            import io.github.brendonmendicino.aformvalidator.core.*
            import io.github.brendonmendicino.aformvalidator.`annotation`.annotations.*
            import io.github.brendonmendicino.aformvalidator.`annotation`.error.*
            import io.github.brendonmendicino.aformvalidator.`annotation`.validators.*
            
            @FormState
            data class PersonEmail(
                val name: String = "",
                /** 
                  * Ma com'e' bell sta 'mail.
                  * Forza Napoli!
                  */
                @Email 
                @Size(min=2, max=20)
                val personal: String = "test",
                @Min(0)
                val num: Int = 10,
            )
        """.trimIndent(),
        )

        val result = KotlinCompilation().apply {
            sources = annotationSources + listOf(source)
            symbolProcessorProviders = listOf(FormValidatorProcessorProvider())
        }.compile()

//        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `testing quicktest`() {
        val source = SourceFile.kotlin(
            "PersonEmail.kt",
            """
            package io.github.brendonmendicino.aformvalidator.processor
            
            import io.github.brendonmendicino.aformvalidator.core.*
            import io.github.brendonmendicino.aformvalidator.`annotation`.annotations.*
            import io.github.brendonmendicino.aformvalidator.`annotation`.error.*
            import io.github.brendonmendicino.aformvalidator.`annotation`.validators.*
            
            @FormState
            data class PersonEmail(
                val name: String = "",
                /** 
                  * Ma com'e' bell sta 'mail.
                  * Forza Napoli!
                  */
                @Pattern(regex="test")
                val personal: String = "test",
            )
        """.trimIndent(),
        )

        val result = KotlinCompilation().apply {
            sources = annotationsSourcesTest + listOf(source)
            symbolProcessorProviders = listOf(FormValidatorProcessorProvider())
        }.compile()

//        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }
}
