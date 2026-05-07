package es.mundodolphins.app.ui.historical

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import es.mundodolphins.app.R
import es.mundodolphins.app.models.historical.HistoricalGame
import es.mundodolphins.app.ui.historical.components.HistoricalErrorView
import es.mundodolphins.app.ui.historical.components.HistoricalLoadingView
import es.mundodolphins.app.ui.historical.components.LinescoreTable
import es.mundodolphins.app.ui.historical.components.ScoringPlayItem
import es.mundodolphins.app.ui.historical.components.rememberGameResultSummary
import es.mundodolphins.app.ui.theme.TealPrimary
import es.mundodolphins.app.ui.theme.TextDark
import es.mundodolphins.app.ui.theme.TextMuted
import es.mundodolphins.app.viewmodel.GameDetailViewModel
import es.mundodolphins.app.viewmodel.HistoricalUiState

@Composable
fun GameDetailScreen(
    year: Int,
    gameId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(year, gameId) {
        viewModel.loadGame(year, gameId)
    }

    when (val state = uiState) {
        is HistoricalUiState.Error ->
            HistoricalErrorView(
                message = state.message ?: stringResource(R.string.historical_game_not_found),
                onRetry = { viewModel.loadGame(year, gameId, force = true) },
                modifier = modifier,
            )

        HistoricalUiState.Loading -> HistoricalLoadingView(modifier = modifier)
        is HistoricalUiState.Success -> {
            GameDetailContent(
                game = state.data,
                year = year,
                onBack = onBack,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun GameDetailContent(
    game: HistoricalGame,
    year: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resultSummary = rememberGameResultSummary(game)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.historical_back_to_season, year),
                    color = TealPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onBack),
                )
                Text(
                    text = stringResource(R.string.historical_game_badge),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TealPrimary,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text = game.title,
                    fontSize = 24.sp,
                    lineHeight = 29.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    text = resultSummary,
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.historical_section_game_info),
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                listOf(
                    stringResource(R.string.historical_info_week),
                    stringResource(R.string.historical_info_date),
                    stringResource(R.string.historical_info_stadium),
                    stringResource(R.string.historical_info_stadium_type),
                    stringResource(R.string.historical_info_surface),
                    stringResource(R.string.historical_info_weather),
                ).forEach { key ->
                    val value = game.gameInfo[key].orEmpty().ifBlank { stringResource(R.string.historical_dash) }
                    Text(
                        text = stringResource(R.string.historical_key_value, key, value),
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        color = TextDark,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.historical_section_linescore),
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(TealPrimary)
                            .padding(bottom = 8.dp),
                )
                LinescoreTable(rows = game.linescore)
            }
        }

        item {
            Text(
                text = stringResource(R.string.historical_section_scoring_summary),
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        if (game.scoringSummary.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.historical_no_scoring_summary),
                    color = TextMuted,
                )
            }
        } else {
            items(items = game.scoringSummary, key = { it.quarter + it.time + it.description }) { play ->
                ScoringPlayItem(play = play)
            }
        }
    }
}
