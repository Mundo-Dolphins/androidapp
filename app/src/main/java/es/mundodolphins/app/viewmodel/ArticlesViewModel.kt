package es.mundodolphins.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.mundodolphins.app.client.ArticlesService
import es.mundodolphins.app.models.ArticlesResponse
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ArticlesViewModel
    @Inject
    constructor(
        private val articlesService: ArticlesService,
    ) : ViewModel() {
    private val _articles = MutableStateFlow<List<ArticlesResponse>>(emptyList())
    val articles: StateFlow<List<ArticlesResponse>> = _articles
    private var hasLoadedOnce = false

    fun fetchArticles(force: Boolean = false) {
        if (!force && hasLoadedOnce) return
        viewModelScope.launch {
            fetchArticlesSuspend(force)
        }
    }

    /**
     * Suspend function that fetches articles from the service and updates state.
     * Returning Boolean makes it easier to assert success/failure in integration tests.
     */
    suspend fun fetchArticlesSuspend(force: Boolean = false): Boolean {
        if (!force && hasLoadedOnce) return true
        return try {
            val response = articlesService.getArticles()
            _articles.value = response
            hasLoadedOnce = true
            true
        } catch (e: Exception) {
            Log.e("ArticlesViewModel", "Error fetching articles", e)
            false
        }
    }

    fun getArticleByPublishedDate(publishedTimestamp: Long): ArticlesResponse? =
        _articles.value.find {
            it.publishedTimestamp == publishedTimestamp
        }
}
