package es.mundodolphins.app.viewmodel

import es.mundodolphins.app.client.FeedService
import es.mundodolphins.app.data.episodes.TestEpisodeDao
import es.mundodolphins.app.repository.EpisodeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Helper to create an EpisodesViewModel that uses in-memory TestEpisodeDao and a provided FeedService fake
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun createTestEpisodesViewModel(
    initialEpisodes: List<es.mundodolphins.app.data.episodes.Episode> = emptyList(),
    feedService: FeedService,
    ioDispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    mainDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher(),
): EpisodesViewModel =
    EpisodesViewModel(
        EpisodeRepository(TestEpisodeDao(initialEpisodes)),
        feedService,
        ioDispatcher,
        mainDispatcher,
    )
