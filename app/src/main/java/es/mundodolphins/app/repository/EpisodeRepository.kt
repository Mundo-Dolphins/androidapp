package es.mundodolphins.app.repository

import android.util.Log
import es.mundodolphins.app.data.episodes.Episode
import es.mundodolphins.app.data.episodes.Episode.ListeningStatus.LISTENED
import es.mundodolphins.app.data.episodes.Episode.ListeningStatus.LISTENING
import es.mundodolphins.app.data.episodes.Episode.ListeningStatus.NOT_LISTENED
import es.mundodolphins.app.data.episodes.EpisodeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull // Added import

class EpisodeRepository(
    private val episodeDao: EpisodeDao,
) {
    suspend fun insertAllEpisodes(episodes: List<Episode>) = episodeDao.insertAllEpisodes(episodes)

    fun getAllEpisodesIds() = episodeDao.getAllEpisodesIds()

    fun getEpisodeById(episodeId: Long): Flow<Episode?> = episodeDao.getEpisodeById(episodeId)

    fun getFeed(): Flow<List<Episode>> =
        try {
            episodeDao.getFeed()
        } catch (e: Exception) {
            Log.e("EpisodeRepository", "Error getting feed", e)
            emptyFlow<List<Episode>>()
        }

    fun getSeason(seasonId: Int) = episodeDao.getSeason(seasonId)

    fun getSeasons() = episodeDao.getSeasons()

    suspend fun updateEpisodePosition(
        episodeId: Long,
        position: Long,
        hasFinished: Boolean,
    ) {
        val episode = episodeDao.getEpisodeById(episodeId).firstOrNull() // Changed to firstOrNull()
        episode?.let { currentEpisode ->
            val updatedEpisode =
                currentEpisode.copy(
                    listenedProgress = position,
                    listeningStatus =
                        when {
                            hasFinished -> LISTENED
                            // Consider original state if position is 0 and it was already NOT_LISTENED
                            position == 0L &&
                                currentEpisode.listenedProgress == 0L &&
                                currentEpisode.listeningStatus == NOT_LISTENED -> NOT_LISTENED
                            position == 0L -> NOT_LISTENED
                            else -> LISTENING
                        },
                )
            episodeDao.insertEpisode(updatedEpisode)
        }
    }
}
