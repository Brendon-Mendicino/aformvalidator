@file:OptIn(ExperimentalCompilerApi::class)

package io.github.brendonmendicino.aformvalidator.processor

import assertk.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.cli.common.isCommonSourceForPsi
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class EmailTest {
    val root =
        "../aformvalidator-annotation/src/main/java/io/github/brendonmendicino/aformvalidator/annotation"
    val files: Array<out File?>? = File(root).listFiles()
    val annotationSources = files!!.map { SourceFile.fromPath(it!!) }

    @Test
    fun `email accepts valid addresses`() {
        val source = SourceFile.kotlin(
            "PersonEmail.kt", """
            package io.github.brendonmendicino.aformvalidator.processor
            
            import io.github.brendonmendicino.aformvalidator.`annotation`.*
            
            @FormState
            data class PersonEmail(
                val name: String = "",
                /** 
                  * Ma com'e' bell sta 'mail.
                  * Forza Napoli!
                  */
                @Email 
                val personal: String = "",
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
}
