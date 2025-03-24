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
import es.mundodolphins.app.models.Episode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {
    var statusFeed: LoadStatus by mutableStateOf(LoadStatus.LOADING)
        private set

    var feed = emptyList<Episode>()
        private set

    fun getFeed() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = FeedClient.service.getFeed()
                if (response.isSuccessful) {
                    feed = response.body()!!
                    statusFeed = LoadStatus.SUCCESS
                } else {
                    Log.e(
                        "Loading Feed",
                        "Status: ${response.code()}, Body: ${response.errorBody()}, Message: ${response.message()}, URL: ${
                            response.raw().request().url()
                        }"
                    )
                    Firebase.crashlytics.log(
                        "Loading feed failed: " +
                                "Status: ${response.code()}, " +
                                "Body: ${response.errorBody()}, " +
                                "Message: ${response.message()}, " +
                                "URL: ${response.raw().request().url()}"
                    )
                    statusFeed = LoadStatus.ERROR
                }
            } catch (e: Exception) {
                Log.e("Loading Feed", e.message.toString())
                Firebase.crashlytics.recordException(
                    e,
                    CustomKeysAndValues.Builder().putString("process", "feed").build()
                )
                statusFeed = LoadStatus.ERROR
            }
        }
    }

    fun getEpisode(id: Long) = feed.firstOrNull { it.id == id }

    enum class LoadStatus {
        LOADING, SUCCESS, ERROR
    }
}