package es.mundodolphins.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.mundodolphins.app.repository.EpisodeRepository

class PlayerViewModelFactory(private val episodeRepository: EpisodeRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(episodeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}