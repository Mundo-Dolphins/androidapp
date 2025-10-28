package es.mundodolphins.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.mundodolphins.app.client.MundoDolphinsClient
import es.mundodolphins.app.models.ArticlesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticlesViewModel(
    private val articlesServiceProvided: es.mundodolphins.app.client.ArticlesService? = null
) : ViewModel() {
    // Use provided service in tests; fall back to client in production.
    private val articlesService: es.mundodolphins.app.client.ArticlesService =
        articlesServiceProvided ?: MundoDolphinsClient.articlesService
    private val _articles = MutableStateFlow<List<ArticlesResponse>>(emptyList())
    val articles: StateFlow<List<ArticlesResponse>> = _articles

    fun fetchArticles() {
        viewModelScope.launch {
            fetchArticlesSuspend()
        }
    }

    /**
     * Suspend function that fetches articles from the service and updates state.
     * Returning Boolean makes it easier to assert success/failure in integration tests.
     */
    suspend fun fetchArticlesSuspend(): Boolean {
        return try {
            val response = articlesService.getArticles()
            _articles.value = response
            true
        } catch (e: Exception) {
            Log.e("ArticlesViewModel", "Error fetching articles", e)
            false
        }
    }

    fun getArticleByPublishedDate(publishedTimestamp: Long): ArticlesResponse? {
        return _articles.value.find { it.publishedTimestamp == publishedTimestamp }
    }
}