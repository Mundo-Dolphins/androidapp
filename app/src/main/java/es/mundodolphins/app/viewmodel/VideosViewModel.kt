package es.mundodolphins.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.mundodolphins.app.repository.VideosRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VideosViewModel
    @Inject
    constructor(
        private val videosRepository: VideosRepository,
    ) : ViewModel() {

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
