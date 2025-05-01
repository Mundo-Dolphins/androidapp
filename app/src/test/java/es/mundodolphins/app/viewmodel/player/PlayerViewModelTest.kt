package es.mundodolphins.app.viewmodel.player

import android.content.Context
import es.mundodolphins.app.data.Episode
import es.mundodolphins.app.repository.EpisodeRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    private lateinit var episodeRepository: EpisodeRepository
    private lateinit var playerServiceHelper: PlayerServiceHelper
    private lateinit var playerViewModel: PlayerViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        episodeRepository = mockk()
        playerServiceHelper = mockk()
        playerViewModel = PlayerViewModel(episodeRepository, playerServiceHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should bind and start service and update player state`() = runTest {
        val context = mockk<Context>(relaxed = true)
        val episodeId = 1L
        val mp3Url = "https://example.com/audio.mp3"
        val listenedProgress = 5000L
        val episode = mockk<Episode>()

        every { episode.listenedProgress } returns listenedProgress
        coEvery { episodeRepository.getEpisodeById(episodeId) } returns flowOf(episode)
        every {
            playerServiceHelper.bindAndStartService(context, mp3Url, listenedProgress, any())
        } just Runs

        playerViewModel.initializePlayer(context, episodeId, mp3Url)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { episodeRepository.getEpisodeById(episodeId) }
        verify { playerServiceHelper.bindAndStartService(context, mp3Url, listenedProgress, any()) }
    }

    @Test
    fun `should unbind service and save position`() = runTest {
        val context = mockk<Context>()

        coEvery { episodeRepository.updateEpisodePosition(any(), any(), any()) } just Runs
        every { playerServiceHelper.unbindAndStopService(context) } just Runs

        playerViewModel.releasePlayer(context)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { playerServiceHelper.unbindAndStopService(context) }
        coVerify { episodeRepository.updateEpisodePosition(any(), any(), false) }
    }
}