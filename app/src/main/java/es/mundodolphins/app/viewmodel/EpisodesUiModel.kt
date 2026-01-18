package es.mundodolphins.app.viewmodel

import es.mundodolphins.app.data.episodes.Episode
import kotlinx.coroutines.flow.Flow

/**
 * Minimal interface representing the pieces of EpisodesViewModel that UI code consumes.
 * Allows tests to provide fakes without needing to extend the concrete ViewModel implementation.
 */
interface EpisodesUiModel {
    val statusRefresh: EpisodesViewModel.LoadStatus

    val feed: Flow<List<Episode>>

    val season: Flow<List<Episode>>

    val episode: Flow<Episode?>

    fun refreshDatabase()

    fun getEpisode(id: Long)

    fun getSeason(seasonId: Int)
}
