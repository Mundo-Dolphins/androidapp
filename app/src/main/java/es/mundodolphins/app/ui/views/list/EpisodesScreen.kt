package es.mundodolphins.app.ui.views.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.mundodolphins.app.viewmodel.EpisodesViewModel
import es.mundodolphins.app.viewmodel.EpisodesViewModel.LoadStatus.ERROR
import es.mundodolphins.app.viewmodel.EpisodesViewModel.LoadStatus.LOADING
import es.mundodolphins.app.viewmodel.EpisodesViewModel.LoadStatus.SUCCESS

@Composable
fun EpisodesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    model: EpisodesViewModel = viewModel()
) {
    when (model.statusRefresh) {
        SUCCESS -> {
            ListEpisodesView(
                episodes = model.feed.collectAsState(initial = emptyList()).value,
                modifier = modifier,
                navController = navController
            )
        }

        LOADING -> {
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