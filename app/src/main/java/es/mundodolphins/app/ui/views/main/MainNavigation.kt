package es.mundodolphins.app.ui.views.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.views.articles.ArticleScreen
import es.mundodolphins.app.ui.views.articles.ListArticlesView
import es.mundodolphins.app.ui.views.info.EpisodeScreen
import es.mundodolphins.app.ui.views.links.UsefulLinksScreen
import es.mundodolphins.app.ui.views.list.EpisodesScreen
import es.mundodolphins.app.ui.views.seasons.SeasonsListScreen
import es.mundodolphins.app.ui.views.seasons.SeasonsView
import es.mundodolphins.app.viewmodel.ArticlesViewModel
import es.mundodolphins.app.viewmodel.EpisodesViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    episodesViewModel: EpisodesViewModel = viewModel(),
    articlesViewModel: ArticlesViewModel = viewModel(),
    navController: NavHostController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.Feed.route
        ) {
            composable(route = Routes.Feed.route) {
                EpisodesScreen(
                    navController = navController,
                    modifier = modifier,
                    model = episodesViewModel
                )
            }
            composable(route = Routes.EpisodeView.route + "/{id}") { backStackEntry ->
                episodesViewModel.getEpisode(backStackEntry.arguments?.getString("id")?.toLong() ?: 0)
                EpisodeScreen(
                    episode = episodesViewModel.episode.collectAsState(null).value,
                    navController = navController,
                    modifier = modifier
                )
            }
            composable(route = Routes.UsefulLinks.route) {
                UsefulLinksScreen(modifier = modifier)
            }
            composable(route = Routes.SeasonsList.route) {
                SeasonsListScreen(
                    modifier = modifier,
                    navController = navController,
                    model = episodesViewModel
                )
            }
            composable(route = Routes.SeasonView.route + "/{id}") {
                SeasonsView(
                    seasonId = it.arguments?.getString("id")?.toInt() ?: 0,
                    modifier = modifier,
                    navController = navController,
                    model = episodesViewModel
                )
            }

            composable(route = Routes.Articles.route) {
                articlesViewModel.fetchArticles()
                ListArticlesView(
                    articles = articlesViewModel.articles.collectAsState(emptyList()).value,
                    modifier = modifier,
                    navController = navController
                )
            }

            composable(route = Routes.Article.route + "/{publishedTimestamp}") { backStackEntry ->
                val publishedTimestamp = backStackEntry.arguments?.getString("publishedTimestamp")?.toLong() ?: Long.MIN_VALUE

                ArticleScreen(
                    article = articlesViewModel.getArticleByPublishedDate(publishedTimestamp),
                    modifier = modifier,
                    navController = navController
                )
            }

        }
    }
}