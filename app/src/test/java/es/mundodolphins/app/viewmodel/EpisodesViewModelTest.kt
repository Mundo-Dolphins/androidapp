package es.mundodolphins.app.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.client.FeedService
import es.mundodolphins.app.data.episodes.Episode
import es.mundodolphins.app.models.EpisodeResponse
import es.mundodolphins.app.repository.EpisodeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var episodeRepository: EpisodeRepository

    private lateinit var feedService: FeedService

    private lateinit var episodesViewModel: EpisodesViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        episodeRepository = mockk()
        feedService = mockk()
        episodesViewModel = EpisodesViewModel(episodeRepository, feedService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshDatabase should update statusRefresh to SUCCESS when API call is successful`() =
        runTest {
            val mockSeasonResponse = listOf("season_1.json")
            val mockEpisodes = listOf(
                EpisodeResponse(
                    dateAndTime = Instant.now().toString(),
                    description = "Test description",
                    audio = "https://example.com/audio.mp3",
                    imgMain = "https://example.com/img_main.jpg",
                    imgMini = "https://example.com/img_mini.jpg",
                    len = "3600",
                    link = "https://example.com/episode",
                    title = "Test Episode"
                )
            )

            coEvery { feedService.getAllSeasons() } returns mockk {
                every { isSuccessful } returns true
                every { body() } returns mockSeasonResponse
            }
            coEvery { feedService.getSeasonEpisodes(any()) } returns mockk {
                every { isSuccessful } returns true
                every { body() } returns mockEpisodes
            }
            coEvery { episodeRepository.getAllEpisodesIds() } returns emptyList()
            coEvery { episodeRepository.insertAllEpisodes(any()) } returns Unit

            episodesViewModel.refreshDatabase()
            testDispatcher.scheduler.advanceUntilIdle()

            await atMost 1.seconds.toJavaDuration() untilAsserted {
                assertThat(episodesViewModel.statusRefresh).isEqualTo(EpisodesViewModel.LoadStatus.SUCCESS)
            }
            coVerify { episodeRepository.insertAllEpisodes(any()) }
        }

    @Test
    fun `getEpisode should fetch episode by ID`() = runTest {
        val episodeId = 1L
        val mockEpisode = Episode(
            id = episodeId,
            title = "Test Episode",
            description = "This is a test description",
            audio = "https://example.com/audio.mp3",
            published = Instant.parse("2025-02-20T15:44:39Z"),
            imgMain = "https://example.com/img_main.jpg",
            imgMini = "https://example.com/img_mini.jpg",
            len = "3600",
            link = "https://example.com/episode",
            season = 1
        )
        coEvery { episodeRepository.getEpisodeById(episodeId) } returns flowOf(mockEpisode)

        episodesViewModel.getEpisode(episodeId)
        testDispatcher.scheduler.advanceUntilIdle()

        await atMost 1.seconds.toJavaDuration() untilAsserted {
            assertThat(episodesViewModel.episode).isNotNull()
        }
        assertThat(episodesViewModel.episode.first()).isEqualTo(mockEpisode)
        coVerify { episodeRepository.getEpisodeById(episodeId) }
    }

    @Test
    fun `getSeason should fetch season by ID`() = runTest {
        val seasonId = 1
        val mockSeasonEpisodes = listOf(
            Episode(
                id = 1L,
                title = "Test Episode 1",
                description = "Description 1",
                audio = "https://example.com/audio1.mp3",
                published = Instant.parse("2025-02-20T15:44:39Z"),
                imgMain = "https://example.com/img_main1.jpg",
                imgMini = "https://example.com/img_mini1.jpg",
                len = "3600",
                link = "https://example.com/episode1",
                season = seasonId
            ),
            Episode(
                id = 2L,
                title = "Test Episode 2",
                description = "Description 2",
                audio = "https://example.com/audio2.mp3",
                published = Instant.parse("2025-02-21T15:44:39Z"),
                imgMain = "https://example.com/img_main2.jpg",
                imgMini = "https://example.com/img_mini2.jpg",
                len = "3600",
                link = "https://example.com/episode2",
                season = seasonId
            )
        )
        coEvery { episodeRepository.getSeason(seasonId) } returns flowOf(mockSeasonEpisodes)

        episodesViewModel.getSeason(seasonId)
        testDispatcher.scheduler.advanceUntilIdle()

        await atMost 1.seconds.toJavaDuration() untilAsserted {
            assertThat(episodesViewModel.season).isNotNull()
        }
        assertThat(episodesViewModel.season.first()).isEqualTo(mockSeasonEpisodes)
        coVerify { episodeRepository.getSeason(seasonId) }
    }
}