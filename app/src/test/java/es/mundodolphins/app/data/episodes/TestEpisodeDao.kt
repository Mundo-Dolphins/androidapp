package es.mundodolphins.app.data.episodes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** In-memory test implementation of EpisodeDao for unit tests */
class TestEpisodeDao(
    initial: List<Episode> = emptyList(),
) : EpisodeDao {
    private val episodesFlow = MutableStateFlow<List<Episode>>(initial.toMutableList())

    override suspend fun insertEpisode(episode: Episode) {
        val current = episodesFlow.value.toMutableList()
        val idx = current.indexOfFirst { it.id == episode.id }
        if (idx >= 0) current[idx] = episode else current.add(episode)
        episodesFlow.value = current
    }

    override suspend fun insertAllEpisodes(episodes: List<Episode>) {
        val current = episodesFlow.value.toMutableList()
        for (ep in episodes) {
            val idx = current.indexOfFirst { it.id == ep.id }
            if (idx >= 0) current[idx] = ep else current.add(ep)
        }
        episodesFlow.value = current
    }

    override fun getAllEpisodesIds(): List<Long> = episodesFlow.value.map { it.id }

    override fun getEpisodeById(episodeId: Long): Flow<Episode?> =
        MutableStateFlow(episodesFlow.value.firstOrNull { it.id == episodeId }).asStateFlow()

    override fun getFeed(): Flow<List<Episode>> = episodesFlow.asStateFlow()

    override fun getSeasons(): Flow<List<Int>> = MutableStateFlow(episodesFlow.value.map { it.season }.distinct()).asStateFlow()

    override fun getSeason(seasonId: Int): Flow<List<Episode>> =
        MutableStateFlow(episodesFlow.value.filter { it.season == seasonId }).asStateFlow()
}
