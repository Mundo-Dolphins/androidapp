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
    private var hasLoadedOnce = false

    fun fetchVideos(force: Boolean = false) {
        if (!force && hasLoadedOnce) return
        viewModelScope.launch {
            fetchVideosSuspend(force)
        }
    }

    suspend fun fetchVideosSuspend(force: Boolean = false): Boolean {
        if (!force && hasLoadedOnce) return true
        _status.value = LoadStatus.LOADING
        return try {
            val response = videosRepository.getVideos()
            _videos.value = response
            _status.value = if (response.isEmpty()) LoadStatus.EMPTY else LoadStatus.SUCCESS
            hasLoadedOnce = true
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
