package es.mundodolphins.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.mundodolphins.app.client.MundoDolphinsClient
import es.mundodolphins.app.repository.VideosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideosViewModel(
    private val videosRepositoryProvided: VideosRepository? = null,
) : ViewModel() {
    private val videosRepository = videosRepositoryProvided ?: VideosRepository(MundoDolphinsClient.videosService)

    private val _videos = MutableStateFlow<List<VideoUiModel>>(emptyList())
    val videos: StateFlow<List<VideoUiModel>> = _videos

    private val _status = MutableStateFlow(LoadStatus.LOADING)
    val status: StateFlow<LoadStatus> = _status

    fun fetchVideos() {
        viewModelScope.launch {
            fetchVideosSuspend()
        }
    }

    suspend fun fetchVideosSuspend(): Boolean {
        _status.value = LoadStatus.LOADING
        return try {
            val response = videosRepository.getVideos()
            _videos.value = response
            _status.value = if (response.isEmpty()) LoadStatus.EMPTY else LoadStatus.SUCCESS
            true
        } catch (e: Exception) {
            Log.e("VideosViewModel", "Error fetching videos", e)
            _status.value = LoadStatus.ERROR
            false
        }
    }

    enum class LoadStatus {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY,
    }
}
