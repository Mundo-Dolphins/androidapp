package es.mundodolphins.app.ui.historical

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.historical.components.HistoricalErrorView
import es.mundodolphins.app.ui.historical.components.HistoricalLoadingView
import es.mundodolphins.app.ui.historical.components.SeasonCard
import es.mundodolphins.app.viewmodel.HistoricalSeasonsViewModel
import es.mundodolphins.app.viewmodel.HistoricalUiState

@Composable
fun HistoricalSeasonsScreen(
    onSeasonClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoricalSeasonsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSeasons()
    }

    when (val state = uiState) {
        is HistoricalUiState.Error -> {
            HistoricalErrorView(
                message = state.message,
                onRetry = { viewModel.loadSeasons(force = true) },
                modifier = modifier,
            )
        }

        HistoricalUiState.Loading -> HistoricalLoadingView(modifier = modifier)
        is HistoricalUiState.Success -> HistoricalSeasonsContent(seasons = state.data, onSeasonClick = onSeasonClick, modifier = modifier)
    }
}

@Composable
private fun HistoricalSeasonsContent(
    seasons: List<es.mundodolphins.app.models.historical.HistoricalSeasonSummary>,
    onSeasonClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 3 else 2

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = {
            androidx.compose.foundation.lazy.grid
                .GridItemSpan(maxLineSpan)
        }) {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = stringResource(R.string.historical),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        items(items = seasons, key = { it.year }) { season ->
            SeasonCard(
                season = season,
                onClick = onSeasonClick,
            )
        }
    }
}
