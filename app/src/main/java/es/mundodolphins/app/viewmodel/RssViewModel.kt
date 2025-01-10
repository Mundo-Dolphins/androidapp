package es.mundodolphins.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.mundodolphins.app.client.RssClient
import es.mundodolphins.app.models.RssFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RssViewModel : ViewModel() {
    var statusFeed: LoadStatus by mutableStateOf(LoadStatus.LOADING)
        private set

    var feed = RssFeed(items = emptyList(), status = "ok", feed = null)
        private set

    fun getFeed() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RssClient.service.getFeed()
                if (response.isSuccessful) {
                    feed = response.body()!!
                    statusFeed = LoadStatus.SUCCESS
                } else {
                    statusFeed = LoadStatus.ERROR
                }
            } catch (e: Exception) {
                statusFeed = LoadStatus.ERROR
            }
        }
    }

    fun getEpisode(id: String) = feed.items.firstOrNull { it.id == id }

    enum class LoadStatus {
        LOADING, SUCCESS, ERROR
    }
}