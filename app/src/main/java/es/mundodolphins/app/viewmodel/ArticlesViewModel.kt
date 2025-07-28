package es.mundodolphins.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.mundodolphins.app.client.MundoDolphinsClient
import es.mundodolphins.app.models.ArticlesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticlesViewModel : ViewModel() {
    private val _articles = MutableStateFlow<List<ArticlesResponse>>(emptyList())
    val articles: StateFlow<List<ArticlesResponse>> = _articles

    fun fetchArticles() {
        viewModelScope.launch {
            try {
                val response = MundoDolphinsClient.articlesService.getArticles()
                _articles.value = response
            } catch (e: Exception) {
                Log.e("ArticlesViewModel", "Error fetching articles", e)
            }
        }
    }

    fun getArticleByPublishedDate(publishedTimestamp: Long): ArticlesResponse? {
        return _articles.value.find { it.publishedTimestamp == publishedTimestamp }
    }
}