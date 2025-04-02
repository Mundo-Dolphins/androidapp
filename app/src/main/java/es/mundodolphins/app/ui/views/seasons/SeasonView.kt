package es.mundodolphins.app.ui.views.seasons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.mundodolphins.app.ui.views.list.ListEpisodesView
import es.mundodolphins.app.viewmodel.FeedViewModel
import es.mundodolphins.app.viewmodel.FeedViewModel.LoadStatus.ERROR
import es.mundodolphins.app.viewmodel.FeedViewModel.LoadStatus.LOADING
import es.mundodolphins.app.viewmodel.FeedViewModel.LoadStatus.SUCCESS

@Composable
fun SeasonsView(
    seasonId: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
    model: FeedViewModel = viewModel()
) {
    when (model.statusSeason) {
        SUCCESS -> {
            model.season[seasonId].takeIf { it != null }?.let {
                ListEpisodesView(
                    episodes = model.season[seasonId]!!,
                    modifier = modifier,
                    navController = navController
                )
            } ?: Text("Error")
        }

        LOADING -> {
            model.getSeason(seasonId)
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