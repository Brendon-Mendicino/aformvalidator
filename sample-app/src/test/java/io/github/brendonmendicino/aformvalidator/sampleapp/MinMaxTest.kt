package io.github.brendonmendicino.aformvalidator.sampleapp
import io.github.brendonmendicino.aformvalidator.annotation.FormState
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Max
import io.github.brendonmendicino.aformvalidator.annotation.annotations.Min
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MinMaxTest {
    @FormState
    data class PointMin(@Min(0) val x: Int, @Min(0) val y: Int?)

    @FormState
    data class PointMax(@Max(10) val x: Int, @Max(10) val y: Int?)

    @Test
    fun min_kdoc_sequence_uses_error() {
        var state = PointMin(-10, null).toValidator()
        assertNotNull("x below min → error", state.x.error)
        assertNull("y null passes", state.y.error)

        state = state.copy(x = state.x.update(5))
        assertNull("x within bounds → ok", state.x.error)
    }

    @Test
    fun max_kdoc_sequence_uses_error() {
        var state = PointMax(20, null).toValidator()
        assertNotNull("x above max → error", state.x.error)
        assertNull("y null passes", state.y.error)

        state = state.copy(x = state.x.update(5))
        assertNull("x within bounds → ok", state.x.error)
    }
}

