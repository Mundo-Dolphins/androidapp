package es.mundodolphins.app.viewmodel

import kotlinx.coroutines.test.runTest
import org.junit.Test
 import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import retrofit2.Response
import es.mundodolphins.app.client.FeedService
import es.mundodolphins.app.models.EpisodeResponse
import kotlinx.coroutines.flow.first
import okhttp3.ResponseBody.Companion.toResponseBody

// Fake implementations of FeedService for testing
class FakeFeedServiceSuccess : FeedService {
    override suspend fun getAllSeasons(): Response<List<String>> {
        return Response.success(listOf("season_1.json"))
    }

    override suspend fun getSeasonEpisodes(id: Int): Response<List<EpisodeResponse>> {
        val episode = EpisodeResponse(
            dateAndTime = "2020-01-01T00:00:00Z",
            description = "desc",
            audio = "audio",
            imgMain = "imgMain",
            imgMini = "imgMini",
            len = "00:10:00",
            link = "link",
            title = "title",
        )
        return Response.success(listOf(episode))
    }
}

class FakeFeedServiceEmpty : FeedService {
    override suspend fun getAllSeasons(): Response<List<String>> = Response.success(emptyList())
    override suspend fun getSeasonEpisodes(id: Int): Response<List<EpisodeResponse>> = Response.success(emptyList())
}

class FakeFeedServiceError : FeedService {
    override suspend fun getAllSeasons(): Response<List<String>> = Response.error(500, "error".toResponseBody())
    override suspend fun getSeasonEpisodes(id: Int): Response<List<EpisodeResponse>> = Response.error(500, "error".toResponseBody())
}

class EpisodesViewModelTest {

    @Test
    fun refreshDatabase_success_setsStatusSuccess_and_insertsEpisodes() = runTest {
        val viewModel = createTestEpisodesViewModel(feedService = FakeFeedServiceSuccess())

        // call suspend function directly
        viewModel.refreshDatabaseBlocking()

        assertEquals(
            "Expected SUCCESS but got ${viewModel.statusRefresh}. lastError=${viewModel.lastError}",
            EpisodesViewModel.LoadStatus.SUCCESS,
            viewModel.statusRefresh,
        )

        // feed is a Flow<List<Episode>>; ensure it contains the inserted episode
        val list = viewModel.feed.first()
        assertTrue(list.isNotEmpty())
        assertEquals("title", list.first().title)
    }

    @Test
    fun refreshDatabase_empty_setsStatusEmpty() = runTest {
        val viewModel = createTestEpisodesViewModel(feedService = FakeFeedServiceEmpty())

        viewModel.refreshDatabaseBlocking()

        assertEquals(
            "Expected EMPTY but got ${viewModel.statusRefresh}. lastError=${viewModel.lastError}",
            EpisodesViewModel.LoadStatus.EMPTY,
            viewModel.statusRefresh,
        )
    }

    @Test
    fun refreshDatabase_error_setsStatusError() = runTest {
        val viewModel = createTestEpisodesViewModel(feedService = FakeFeedServiceError())

        viewModel.refreshDatabaseBlocking()

        assertEquals(
            "Expected ERROR but got ${viewModel.statusRefresh}. lastError=${viewModel.lastError}",
            EpisodesViewModel.LoadStatus.ERROR,
            viewModel.statusRefresh,
        )
    }
}
