package es.mundodolphins.app.data.episodes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** In-memory test implementation of EpisodeDao for unit tests */
class TestEpisodeDao(initial: List<Episode> = emptyList()) : EpisodeDao {
    private val _episodes = MutableStateFlow<List<Episode>>(initial.toMutableList())

    override suspend fun insertEpisode(episode: Episode) {
        val current = _episodes.value.toMutableList()
        val idx = current.indexOfFirst { it.id == episode.id }
        if (idx >= 0) current[idx] = episode else current.add(episode)
        _episodes.value = current
    }

    override suspend fun insertAllEpisodes(episodes: List<Episode>) {
        val current = _episodes.value.toMutableList()
        for (ep in episodes) {
            val idx = current.indexOfFirst { it.id == ep.id }
            if (idx >= 0) current[idx] = ep else current.add(ep)
        }
        _episodes.value = current
    }

    override fun getAllEpisodesIds(): List<Long> = _episodes.value.map { it.id }

    override fun getEpisodeById(episodeId: Long): Flow<Episode?> =
        MutableStateFlow(_episodes.value.firstOrNull { it.id == episodeId }).asStateFlow()

    override fun getFeed(): Flow<List<Episode>> = _episodes.asStateFlow()

    override fun getSeasons(): Flow<List<Int>> =
        MutableStateFlow(_episodes.value.map { it.season }.distinct()).asStateFlow()

    override fun getSeason(seasonId: Int): Flow<List<Episode>> =
        MutableStateFlow(_episodes.value.filter { it.season == seasonId }).asStateFlow()
}
