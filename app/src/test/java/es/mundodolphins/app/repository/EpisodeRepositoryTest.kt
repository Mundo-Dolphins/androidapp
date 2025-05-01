package es.mundodolphins.app.repository

import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.data.Episode
import es.mundodolphins.app.data.Episode.ListeningStatus.LISTENING
import es.mundodolphins.app.data.Episode.ListeningStatus.NOT_LISTENED
import es.mundodolphins.app.data.EpisodeDao
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.Instant

class EpisodeRepositoryTest {
    private lateinit var episodeDao: EpisodeDao

    private lateinit var episodeRepository: EpisodeRepository

    @Before
    fun setup() {
        episodeDao = mockk()
        episodeRepository = EpisodeRepository(episodeDao)
    }

    @Test
    fun `should save all episodes`() = runBlocking {
        val episodes = getTestEpisodes(2)

        coEvery { episodeDao.insertAllEpisodes(episodes) } just Runs

        episodeRepository.insertAllEpisodes(episodes)

        coVerify(exactly = 1) { episodeDao.insertAllEpisodes(episodes) }
    }

    @Test
    fun `should get all episode IDs`() {
        val episodeIds = listOf(1L, 2L, 3L)
        every { episodeDao.getAllEpisodesIds() } returns episodeIds

        assertThat(episodeRepository.getAllEpisodesIds()).isEqualTo(episodeIds)
        verify(exactly = 1) { episodeDao.getAllEpisodesIds() }
    }

    @Test
    fun `should return the episode by given ID`() {
        val episodeId = 1L
        val episode = getTestEpisodes(1).first()
        every { episodeDao.getEpisodeById(episodeId) } returns flowOf(episode)

        runBlocking {
            val actual = episodeRepository.getEpisodeById(episodeId).first()
            assertThat(actual).isEqualTo(episode)
        }

        verify(exactly = 1) { episodeDao.getEpisodeById(episodeId) }
    }

    @Test
    fun `should return the feed of episodes`() {
        val episodes = getTestEpisodes(2)
        every { episodeDao.getFeed() } returns flowOf(episodes)

        runBlocking {
            val actual = episodeRepository.getFeed().first()
            assertThat(actual).isEqualTo(episodes)
        }

        verify(exactly = 1) { episodeDao.getFeed() }
    }

    @Test
    fun `should update the episode position`() = runBlocking {
        val episodeId = 1L
        val episode = getTestEpisodes(1).first()
        every { episodeDao.getEpisodeById(episodeId) } returns flowOf(episode)
        coEvery { episodeDao.insertEpisode(any()) } just Runs

        episodeRepository.updateEpisodePosition(episodeId, 50L, hasFinished = false)

        coVerify(exactly = 1) {
            episodeDao.insertEpisode(
                episode.copy(
                    listenedProgress = 50L,
                    listeningStatus = LISTENING
                )
            )
        }
    }

    private fun getTestEpisodes(takeEpisodes: Int = 1) = listOf(
        Episode(
            id = 1,
            title = "Test Episode",
            description = "This is a test episode",
            audio = "http://example.com/episode.mp3",
            listenedProgress = 0,
            published = Instant.parse("2025-02-20T15:44:39Z"),
            imgMain = "https://wwww.image.com/1.jpg",
            imgMini = "TODO()",
            len = "01:29:55",
            link = "https://www.episode.com/1",
            season = 1,
            listeningStatus = NOT_LISTENED,
        ),
        Episode(
            id = 2,
            title = "Test Episode",
            description = "This is a test episode",
            audio = "http://example.com/episode.mp3",
            listenedProgress = 0,
            published = Instant.parse("2025-02-20T15:44:39Z"),
            imgMain = "https://wwww.image.com/2.jpg",
            imgMini = "TODO()",
            len = "01:29:55",
            link = "https://www.episode.com/2",
            season = 1,
            listeningStatus = LISTENING,
        )
    ).take(takeEpisodes)
}