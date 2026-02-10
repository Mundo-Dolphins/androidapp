package es.mundodolphins.app.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class YoutubeVideoUtilsTest {
    @Test
    fun `extractYoutubeVideoId supports watch youtu and embed urls`() {
        val watchWithParams = "https://www.youtube.com/watch?v=8SUjlSJ-Cbs&pp=ygUQbXVuZG8gZG9scGhpbnM%3D"
        val shortUrl = "https://youtu.be/8SUjlSJ-Cbs?si=test"
        val embedUrl = "https://www.youtube.com/embed/8SUjlSJ-Cbs?start=12"

        assertThat(extractYoutubeVideoId(watchWithParams)).isEqualTo("8SUjlSJ-Cbs")
        assertThat(extractYoutubeVideoId(shortUrl)).isEqualTo("8SUjlSJ-Cbs")
        assertThat(extractYoutubeVideoId(embedUrl)).isEqualTo("8SUjlSJ-Cbs")
        assertThat(extractYoutubeVideoId("https://example.com/video")).isNull()
    }

    @Test
    fun `thumbnail urls build maxres and hq urls`() {
        val videoId = "8SUjlSJ-Cbs"

        assertThat(buildMaxResThumbnailUrl(videoId))
            .isEqualTo("https://img.youtube.com/vi/8SUjlSJ-Cbs/maxresdefault.jpg")
        assertThat(buildHqThumbnailUrl(videoId))
            .isEqualTo("https://img.youtube.com/vi/8SUjlSJ-Cbs/hqdefault.jpg")
    }
}
