package es.mundodolphins.app.ui.views.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.mundodolphins.app.observer.ConnectivityObserver
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.views.info.EpisodeScreen
import es.mundodolphins.app.ui.views.list.EpisodesScreen
import es.mundodolphins.app.viewmodel.FeedViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(modifier: Modifier = Modifier, model: FeedViewModel = viewModel()) {
    val context = LocalContext.current
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val isConnected by connectivityObserver.isConnected.observeAsState(initial = true)
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        if (isConnected) {
            NavHost(
                navController = navController,
                startDestination = Routes.EpisodesList.route
            ) {
                composable(route = Routes.EpisodesList.route) {
                    EpisodesScreen(navController = navController, modifier = modifier, model = model)
                }
                composable(route = Routes.EpisodeView.route + "/{id}") { backStackEntry ->
                    EpisodeScreen(
                        episode = model.getEpisode(backStackEntry.arguments?.getString("id")?.toLong() ?: 0),
                        modifier = modifier
                    )
                }
            }
        } else {
            Text(
                text = "No internet connection",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}