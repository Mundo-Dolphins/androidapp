package es.mundodolphins.app.ui.views.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.mundodolphins.app.viewmodel.FeedViewModel
import es.mundodolphins.app.viewmodel.FeedViewModel.LoadStatus.ERROR
import es.mundodolphins.app.viewmodel.FeedViewModel.LoadStatus.LOADING
import es.mundodolphins.app.viewmodel.FeedViewModel.LoadStatus.SUCCESS

@Composable
fun EpisodesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    model: FeedViewModel = viewModel()
) {
    when (model.statusFeed) {
        SUCCESS -> {
            ListEpisodesView(
                episodes = model.feed,
                modifier = modifier,
                navController = navController
            )
        }

        LOADING -> {
            model.getFeed()
            Box(
                contentAlignment = Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize(0.5F))
            }
        }

        ERROR -> Text("Error")
    }
}