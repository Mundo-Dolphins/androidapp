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
import es.mundodolphins.app.viewmodel.RssViewModel
import es.mundodolphins.app.viewmodel.RssViewModel.LoadStatus.ERROR
import es.mundodolphins.app.viewmodel.RssViewModel.LoadStatus.LOADING
import es.mundodolphins.app.viewmodel.RssViewModel.LoadStatus.SUCCESS

@Composable
fun EpisodesScreen(
    model: RssViewModel = viewModel(),
    modifier: Modifier = Modifier,
    navController: NavController
) {
    when (model.statusFeed) {
        SUCCESS -> {
            ListEpisodesView(
                episodes = model.feed.items,
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