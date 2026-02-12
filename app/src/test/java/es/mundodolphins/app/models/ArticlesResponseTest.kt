package es.mundodolphins.app.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ArticlesResponseTest {
    @Test
    fun `publishedTimestamp returns null when date is invalid`() {
        val article =
            ArticlesResponse(
                title = "Title",
                author = "Author",
                publishedDate = "not-a-date",
                content = "Body",
            )

        assertThat(article.publishedTimestamp).isNull()
    }
}
