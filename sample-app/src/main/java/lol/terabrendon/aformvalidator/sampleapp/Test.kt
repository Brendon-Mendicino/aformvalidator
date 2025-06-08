package lol.terabrendon.aformvalidator.sampleapp

import lol.terabrendon.aformvalidator.annotation.FormState
import lol.terabrendon.aformvalidator.annotation.Validator
import lol.terabrendon.aformvalidator.annotation.ValidatorCond


@FormState
data class Test(
    @Validator<String>(
        value = Val::class,
        errorType = String::class,
    )
    val niceInt: Int = 0,
    @Validator<String>(
        value = Val::class,
        errorType = String::class,
    )
    val nicerInt: Int = 10,
) {
    companion object {
        class Val : ValidatorCond<Int, String> {
            override val conditions: List<(Int) -> String?> = listOf(
                { it.toString() },
                { it.toString() },
            )
        }

        class IntVal : ValidatorCond<Int, Int> {
            override val conditions: List<(Int) -> Int?> = listOf(
                { it },
                { it },
            )
        }
    }
}
