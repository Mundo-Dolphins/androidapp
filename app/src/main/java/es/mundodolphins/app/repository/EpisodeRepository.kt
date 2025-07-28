package es.mundodolphins.app.repository

import android.util.Log
import es.mundodolphins.app.data.episodes.Episode
import es.mundodolphins.app.data.episodes.Episode.ListeningStatus.LISTENED
import es.mundodolphins.app.data.episodes.Episode.ListeningStatus.LISTENING
import es.mundodolphins.app.data.episodes.Episode.ListeningStatus.NOT_LISTENED
import es.mundodolphins.app.data.episodes.EpisodeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first

class EpisodeRepository(private val episodeDao: EpisodeDao) {

    suspend fun insertAllEpisodes(episodes: List<Episode>) = episodeDao.insertAllEpisodes(episodes)

    fun getAllEpisodesIds() = episodeDao.getAllEpisodesIds()

    fun getEpisodeById(episodeId: Long): Flow<Episode> = episodeDao.getEpisodeById(episodeId)

    fun getFeed(): Flow<List<Episode>> = try {
        episodeDao.getFeed()
    } catch (e: Exception) {
        Log.e("EpisodeRepository", "Error getting feed", e)
        emptyFlow<List<Episode>>()
    }

    fun getSeason(seasonId: Int) = episodeDao.getSeason(seasonId)

    fun getSeasons() = episodeDao.getSeasons()

    suspend fun updateEpisodePosition(episodeId: Long, position: Long, hasFinished: Boolean) {
        episodeDao.getEpisodeById(episodeId).first {
            episodeDao.insertEpisode(
                it.copy(
                    listenedProgress = position,
                    listeningStatus = when {
                        hasFinished -> LISTENED
                        position == 0L -> NOT_LISTENED
                        else -> LISTENING
                    }
                )
            )
            true
        }
    }
}