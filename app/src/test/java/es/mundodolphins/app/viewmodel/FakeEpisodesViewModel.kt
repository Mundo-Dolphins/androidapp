package es.mundodolphins.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import es.mundodolphins.app.data.episodes.Episode

/**
 * Test double that exposes the same public properties the UI consumes.
 * It does not extend the real ViewModel to avoid needing repository/service plumbing.
 */
class FakeEpisodesViewModel(
    episodesListParam: List<Episode> = emptyList(),
    initialStatus: EpisodesViewModel.LoadStatus = EpisodesViewModel.LoadStatus.SUCCESS,
) : EpisodesUiModel {
    // Keep a backing property so helper methods can reference the list
    private val episodesList: List<Episode> = episodesListParam

    override var statusRefresh: EpisodesViewModel.LoadStatus by mutableStateOf(initialStatus)

    // Expose flows the UI reads
    override val feed: Flow<List<Episode>> = flowOf(episodesList)
    override var season: Flow<List<Episode>> by mutableStateOf(emptyFlow())
    override var episode: Flow<Episode?> by mutableStateOf(emptyFlow())

    // No-op for tests
    override fun refreshDatabase() = Unit

    // Allow tests to simulate selecting an episode/season
    override fun getEpisode(id: Long) {
        val found = episodesList.firstOrNull { it.id == id }
        episode = flowOf(found)
    }

    override fun getSeason(seasonId: Int) {
        season = flowOf(episodesList.filter { it.season == seasonId })
    }
}
