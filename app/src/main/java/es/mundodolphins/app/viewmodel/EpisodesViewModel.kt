package es.mundodolphins.app.viewmodel

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.crashlytics
import es.mundodolphins.app.client.FeedService
import es.mundodolphins.app.data.episodes.Episode
import es.mundodolphins.app.models.EpisodeResponse
import es.mundodolphins.app.repository.EpisodeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodesViewModel(
    private val episodeRepository: EpisodeRepository,
    private val feedService: FeedService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel(), EpisodesUiModel {
    override var statusRefresh: LoadStatus by mutableStateOf(LoadStatus.LOADING)
        private set

    // Visible for tests to inspect exceptions
    @VisibleForTesting
    var lastError: String? = null
        private set

    override val feed: Flow<List<Episode>> by lazy { episodeRepository.getFeed() }

    val seasons: Flow<List<Int>> by lazy { episodeRepository.getSeasons() }

    override var season: Flow<List<Episode>> by mutableStateOf(emptyFlow())

    override var episode: Flow<Episode?> by mutableStateOf(emptyFlow())

    override fun refreshDatabase() {
        viewModelScope.launch {
            // Keep launching on viewModelScope, delegate to suspend function
            refreshDatabaseBlocking()
        }
    }

    // Simple safe logging helpers to avoid RuntimeException in JVM unit tests where android.util.Log is not mocked
    private fun safeLogI(tag: String, msg: String) {
        try {
            Log.i(tag, msg)
        } catch (_: Throwable) {
            // ignore logging failures in unit test environment
        }
    }

    private fun safeLogE(tag: String, msg: String, t: Throwable? = null) {
        try {
            if (t != null) Log.e(tag, msg, t) else Log.e(tag, msg)
        } catch (_: Throwable) {
            // ignore
        }
    }

    // Public suspend function to perform refresh; tests can call this inside runTest for deterministic behavior
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun refreshDatabaseBlocking() {
        // Execute network and DB operations on the provided ioDispatcher
        try {
            withContext(ioDispatcher) {
                val response = feedService.getAllSeasons()
                if (response.isSuccessful) {
                    val seasonsBody = response.body()
                    if (seasonsBody == null || seasonsBody.isEmpty()) {
                        safeLogI("Refreshing Database", "No seasons found or body is null")
                        withContext(mainDispatcher) { statusRefresh = LoadStatus.EMPTY }
                    } else {
                        seasonsBody.apply {
                            safeLogI("Refreshing Database", "Found ${this.size} seasons")
                            val episodes = episodeRepository.getAllEpisodesIds()
                            map { it.convertJsonFilenameToSeason() }.forEach { season ->
                                val seasonEpisodes = feedService.getSeasonEpisodes(season)
                                if (seasonEpisodes.isSuccessful) {
                                    safeLogI(
                                        "Refreshing Database",
                                        "Found ${seasonEpisodes.body()!!.size} episodes for season $season",
                                    )
                                    episodeRepository.insertAllEpisodes(
                                        seasonEpisodes
                                            .body()!!
                                            .filter { !episodes.contains(it.id) }
                                            .map { it.toEpisode(season) },
                                    )
                                }
                            }
                        }
                        withContext(mainDispatcher) { statusRefresh = LoadStatus.SUCCESS }
                    }
                } else {
                    // Mark as ERROR first so crashlytics failures don't prevent the state update
                    withContext(mainDispatcher) { statusRefresh = LoadStatus.ERROR }
                    try {
                        safeLogE(
                            "Refreshing Database",
                            "Status: ${response.code()}, Body: ${response.errorBody()}, Message: ${response.message()}, URL: ${
                                response.raw().request.url
                            }",
                        )
                    } catch (_: Exception) {
                        // Ignore logging errors
                    }
                    try {
                        Firebase.crashlytics.log(
                            "Refreshing Database: " +
                                "Status: ${response.code()}, " +
                                "Body: ${response.errorBody()}, " +
                                "Message: ${response.message()}, " +
                                "URL: ${response.raw().request.url}",
                        )
                    } catch (_: Exception) {
                        // Ignore crashlytics errors in test environment
                    }
                }
            }
        } catch (e: Exception) {
            // Ensure status is set to ERROR even if crashlytics logging fails
            withContext(mainDispatcher) { statusRefresh = LoadStatus.ERROR }
            // Store stacktrace for tests
            try {
                lastError = e.stackTraceToString()
            } catch (_: Exception) {
            }
            // Debug: print stacktrace so test output captures reason for ERROR
            try {
                // Avoid direct printStackTrace() usage; use safe logging helper
                safeLogE("Loading Feed", e.message.toString(), e)
            } catch (_: Exception) {
            }
            try {
                safeLogE("Loading Feed", e.message.toString(), e)
            } catch (_: Exception) {
                // ignore
            }
            try {
                Firebase.crashlytics.recordException(
                    e,
                    CustomKeysAndValues.Builder().putString("process", "feed").build(),
                )
            } catch (_: Exception) {
                // ignore crashlytics errors in test environment
            }
        }
    }

    override fun getEpisode(id: Long) {
        viewModelScope.launch(ioDispatcher) {
            // Use injected dispatcher
            episode = episodeRepository.getEpisodeById(id)
        }
    }

    override fun getSeason(seasonId: Int) {
        viewModelScope.launch(ioDispatcher) {
            // Use injected dispatcher
            season = episodeRepository.getSeason(seasonId)
        }
    }

    private fun EpisodeResponse.toEpisode(seasonId: Int) =
        Episode(
            id = id,
            title = title,
            description = description,
            audio = audio,
            published = pubDateTime,
            imgMain = imgMain,
            imgMini = imgMini,
            len = len,
            link = link,
            season = seasonId,
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
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY,
    }
}
