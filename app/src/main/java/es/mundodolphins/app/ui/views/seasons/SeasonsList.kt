package es.mundodolphins.app.ui.views.seasons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.viewmodel.EpisodesViewModel

@Composable
fun SeasonsListScreen(
    modifier: Modifier = Modifier,
    model: EpisodesViewModel = viewModel(),
    navController: NavController
) {
    SeasonsList(
        seasons = model.seasons.collectAsState(initial = emptyList()).value,
        modifier = modifier,
        navController = navController
    )
}

@Composable
fun SeasonsList(
    seasons: List<Int>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .background(color = colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(seasons) {
            SeasonRow(it, navController)
        }
    }
}

@Composable
fun SeasonRow(seasonId: Int, navController: NavController) {
    if (seasonId > 0) {
        Button(onClick = {
            navController.navigate(Routes.SeasonView.route + "/$seasonId")
        }) {
            Text(text = stringResource(R.string.season, seasonId))
        }
    }
}

@Preview
@Composable
fun SeasonsListPreview() {
    SeasonsList(
        listOf(7, 6, 5, 4, 3, 2, 1),
        navController = NavController(LocalContext.current)
    )
}
