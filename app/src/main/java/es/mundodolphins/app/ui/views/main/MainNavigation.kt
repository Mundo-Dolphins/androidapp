package es.mundodolphins.app.ui.views.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.views.info.EpisodeScreen
import es.mundodolphins.app.ui.views.list.EpisodesScreen
import es.mundodolphins.app.viewmodel.FeedViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(model: FeedViewModel = viewModel(), modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.EpisodesList.route
    )
    {
        composable(route = Routes.EpisodesList.route) {
            EpisodesScreen(model = model, navController = navController, modifier = modifier)
        }
        composable(route = Routes.EpisodeView.route + "/{id}") { backStackEntry ->
            EpisodeScreen(
                episode = model.getEpisode(backStackEntry.arguments?.getString("id")?.toLong() ?: 0),
                modifier = modifier
            )
        }
    }
}