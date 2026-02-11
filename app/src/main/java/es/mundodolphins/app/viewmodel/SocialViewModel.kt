package es.mundodolphins.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.mundodolphins.app.repository.SocialRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SocialViewModel
    @Inject
    constructor(
        private val socialRepository: SocialRepository,
    ) : ViewModel() {

    private val _posts = MutableStateFlow<List<SocialUiModel>>(emptyList())
    val posts: StateFlow<List<SocialUiModel>> = _posts

    private val _status = MutableStateFlow(LoadStatus.LOADING)
    val status: StateFlow<LoadStatus> = _status

    fun fetchSocialPosts() {
        viewModelScope.launch {
            fetchSocialPostsSuspend()
        }
    }

    suspend fun fetchSocialPostsSuspend(): Boolean {
        _status.value = LoadStatus.LOADING
        return try {
            val response = socialRepository.getSocialPosts()
            _posts.value = response
            _status.value = if (response.isEmpty()) LoadStatus.EMPTY else LoadStatus.SUCCESS
            true
        } catch (e: Exception) {
            Log.e("SocialViewModel", "Error fetching social posts", e)
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
