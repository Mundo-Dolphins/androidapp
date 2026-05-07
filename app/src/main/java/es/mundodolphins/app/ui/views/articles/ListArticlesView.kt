package es.mundodolphins.app.ui.views.articles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.mundodolphins.app.models.ArticlesResponse

@Composable
fun ListArticlesView(
    articles: List<ArticlesResponse>,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.background(color = colorScheme.background),
        contentPadding = PaddingValues(vertical = 10.dp),
    ) {
        items(articles) {
            ArticleRow(it, navController)
        }
    }
}
