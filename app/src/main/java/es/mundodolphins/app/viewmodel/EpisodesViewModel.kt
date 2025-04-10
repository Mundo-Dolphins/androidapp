package es.mundodolphins.app.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.crashlytics
import es.mundodolphins.app.client.FeedClient
import es.mundodolphins.app.data.Episode
import es.mundodolphins.app.models.EpisodeResponse
import es.mundodolphins.app.repository.EpisodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class EpisodesViewModel(private val episodeRepository: EpisodeRepository) : ViewModel() {
    var statusRefresh: LoadStatus by mutableStateOf(LoadStatus.LOADING)
        private set

    val feed: Flow<List<Episode>> by lazy { episodeRepository.getFeed() }

    val seasons: Flow<List<Int>> by lazy { episodeRepository.getSeasons() }

    var season: Flow<List<Episode>> by mutableStateOf(emptyFlow())

    var episode: Flow<Episode> by mutableStateOf(emptyFlow())

    fun refreshDatabase(lastSeason: Long, forceDownload: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = FeedClient.service.getAllSeasons()
                if (response.isSuccessful) {
                    response.body()!!.apply {
                        Log.i("Refreshing Database", "Found ${this.size} seasons")
                        val episodes = episodeRepository.getAllEpisodesIds()
                        map { it.convertJsonFilenameToSeason() }.forEach { season ->
                            val seasonEpisodes = FeedClient.service.getSeasonEpisodes(season)
                            if (seasonEpisodes.isSuccessful) {
                                Log.i(
                                    "Refreshing Database",
                                    "Found ${seasonEpisodes.body()!!.size} episodes for season $season"
                                )
                                episodeRepository.insertAllEpisodes(
                                    seasonEpisodes.body()!!
                                        .filter { !episodes.contains(it.id) }
                                        .map { it.toEpisode(season) }
                                )
                            }
                        }
                    }
                    statusRefresh = LoadStatus.SUCCESS
                } else {
                    Log.e(
                        "Refreshing Database",
                        "Status: ${response.code()}, Body: ${response.errorBody()}, Message: ${response.message()}, URL: ${
                            response.raw().request.url
                        }"
                    )
                    Firebase.crashlytics.log(
                        "Refreshing Database: " +
                                "Status: ${response.code()}, " +
                                "Body: ${response.errorBody()}, " +
                                "Message: ${response.message()}, " +
                                "URL: ${response.raw().request.url}"
                    )
                    statusRefresh = LoadStatus.ERROR
                }
            } catch (e: Exception) {
                Log.e("Loading Feed", e.message.toString(), e)
                Firebase.crashlytics.recordException(
                    e,
                    CustomKeysAndValues.Builder().putString("process", "feed").build()
                )
                statusRefresh = LoadStatus.ERROR
            }
        }
    }

    fun getEpisode(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            episode = episodeRepository.getEpisodeById(id)
        }
    }

    fun getSeason(seasonId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            season = episodeRepository.getSeason(seasonId)
        }
    }

    private fun EpisodeResponse.toEpisode(seasonId: Int) = Episode(
        id = id,
        title = title,
        description = description,
        audio = audio,
        published = pubDateTime,
        imgMain = imgMain,
        imgMini = imgMini,
        len = len,
        link = link,
        season = seasonId
    )

    private fun String.convertJsonFilenameToSeason(): Int {
        val matchResult = Regex("""season_(\d+)\.json""").find(this)

        return if (matchResult != null) {
            val seasonNumber = matchResult.groupValues[1]
            seasonNumber.toInt()
        } else {
            0
        }
    }

    enum class LoadStatus {
        LOADING, SUCCESS, ERROR
    }
}