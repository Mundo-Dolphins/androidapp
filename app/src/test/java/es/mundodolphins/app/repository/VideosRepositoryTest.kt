package es.mundodolphins.app.repository

import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.client.VideosService
import es.mundodolphins.app.models.VideoResponse
import es.mundodolphins.app.models.VideosResponse
import kotlinx.coroutines.test.runTest
import org.junit.Test

class VideosRepositoryTest {
    @Test
    fun `getVideos maps ui model and sorts by published desc`() =
        runTest {
            val service =
                object : VideosService {
                    override suspend fun getVideos(): VideosResponse =
                        VideosResponse(
                            videos =
                                listOf(
                                    VideoResponse(
                                        duration = "10:00",
                                        isPodcast = false,
                                        publishedAt = "2026-01-10T10:00:00Z",
                                        title = "Older",
                                        url = "https://www.youtube.com/watch?v=AAAAAAAAAAA",
                                        embeddable = true,
                                    ),
                                    VideoResponse(
                                        duration = "12:34",
                                        isPodcast = false,
                                        publishedAt = "2026-02-10T10:00:00Z",
                                        title = "Newer",
                                        url = "https://youtu.be/BBBBBBBBBBB?si=test",
                                        embeddable = true,
                                    ),
                                ),
                            totalCount = 2,
                            lastUpdated = "2026-02-10T15:08:37Z",
                        )
                }

            val repository = VideosRepository(service)

            val result = repository.getVideos()

            assertThat(result).hasSize(2)
            assertThat(result[0].title).isEqualTo("Newer")
            assertThat(result[0].videoId).isEqualTo("BBBBBBBBBBB")
            assertThat(result[0].thumbnailUrl).isEqualTo("https://img.youtube.com/vi/BBBBBBBBBBB/maxresdefault.jpg")
            assertThat(result[0].thumbnailFallbackUrl).isEqualTo("https://img.youtube.com/vi/BBBBBBBBBBB/hqdefault.jpg")
        }
}
