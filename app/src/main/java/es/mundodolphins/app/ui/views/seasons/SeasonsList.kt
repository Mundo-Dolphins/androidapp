package es.mundodolphins.app.ui.views.seasons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.viewmodel.FeedViewModel
import es.mundodolphins.app.viewmodel.FeedViewModel.LoadStatus.ERROR
import es.mundodolphins.app.viewmodel.FeedViewModel.LoadStatus.LOADING
import es.mundodolphins.app.viewmodel.FeedViewModel.LoadStatus.SUCCESS

@Composable
fun SeasonsListScreen(
    modifier: Modifier = Modifier,
    model: FeedViewModel = viewModel(),
    navController: NavController
) {
    when (model.statusSeasons) {
        SUCCESS -> {
            SeasonsList(
                seasons = model.seasons,
                modifier = modifier,
                navController = navController
            )
        }

        LOADING -> {
            model.getSeasons()
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

@Composable
fun SeasonsList(
    seasons: List<String>,
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
fun SeasonRow(s: String, navController: NavController) {
    val seasonId = s.convertJsonFilenameToSeason()
    if (seasonId > 0) {
        Button(onClick = {
            navController.navigate(Routes.SeasonView.route + "/$seasonId")
        }) {
            Text(text = stringResource(R.string.season, seasonId))
        }
    }
}

private fun String.convertJsonFilenameToSeason(): Int {
    val matchResult = Regex("""season_(\d+)\.json""").find(this)

    return if (matchResult != null) {
        val seasonNumber = matchResult.groupValues[1]
        seasonNumber.toInt()
    } else {
        0
    }
}

@Preview
@Composable
fun SeasonsListPreview() {
    SeasonsList(
        listOf(
            "season_7.json",
            "season_6.json",
            "season_5.json",
            "season_4.json",
            "season_3.json",
            "season_2.json",
            "season_1.json"
        ),
        navController = NavController(LocalContext.current)
    )
}
