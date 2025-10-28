package es.mundodolphins.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.mundodolphins.app.client.FeedService
import es.mundodolphins.app.repository.EpisodeRepository

class EpisodesViewModelFactory(
    private val episodeRepository: EpisodeRepository,
    private val feedService: FeedService,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EpisodesViewModel(episodeRepository, feedService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
