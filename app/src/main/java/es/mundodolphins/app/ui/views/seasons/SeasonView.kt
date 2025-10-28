package es.mundodolphins.app.ui.views.seasons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.mundodolphins.app.ui.views.list.ListEpisodesView
import es.mundodolphins.app.viewmodel.EpisodesViewModel

@Composable
fun SeasonsView(
    seasonId: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
    model: EpisodesViewModel = viewModel(),
) {
    model.getSeason(seasonId)
    ListEpisodesView(
        episodes = model.season.collectAsState(emptyList()).value,
        modifier = modifier,
        navController = navController,
    )
}
