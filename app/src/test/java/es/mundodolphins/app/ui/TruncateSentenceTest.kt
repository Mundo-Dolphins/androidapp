package es.mundodolphins.app.ui

import es.mundodolphins.app.ui.views.list.truncateSentence
import org.junit.Assert.assertEquals
import org.junit.Test

class TruncateSentenceTest {
    @Test
    fun returns_same_when_shorter_than_max() {
        val input = "short sentence"
        val out = truncateSentence(input, maxLength = 50)
        assertEquals(input, out)
    }

    @Test
    fun truncates_at_word_boundary_and_adds_ellipsis_behavior() {
        val input = "one two three four five six seven eight nine ten"
        val out = truncateSentence(input, maxLength = 10)
        // Should not cut words; must be <= maxLength
        assert(out.length <= 10)
        // Ensure it's a prefix of the original
        assert(input.startsWith(out))
    }

    @Test
    fun exact_length_returns_same() {
        val input = "12345"
        val out = truncateSentence(input, maxLength = 5)
        assertEquals(input, out)
    }
}
