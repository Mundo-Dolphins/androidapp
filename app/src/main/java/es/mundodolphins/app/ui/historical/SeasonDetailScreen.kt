package es.mundodolphins.app.ui.historical

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import es.mundodolphins.app.R
import es.mundodolphins.app.models.historical.HistoricalSeasonDetail
import es.mundodolphins.app.ui.historical.components.DraftTable
import es.mundodolphins.app.ui.historical.components.HistoricalErrorView
import es.mundodolphins.app.ui.historical.components.HistoricalLoadingView
import es.mundodolphins.app.ui.historical.components.SeasonStatsTables
import es.mundodolphins.app.ui.historical.components.SeasonSummaryTable
import es.mundodolphins.app.ui.historical.components.rememberGameResultSummary
import es.mundodolphins.app.ui.theme.TableBorder
import es.mundodolphins.app.ui.theme.TealPrimary
import es.mundodolphins.app.ui.theme.TextDark
import es.mundodolphins.app.ui.theme.TextMuted
import es.mundodolphins.app.viewmodel.HistoricalUiState
import es.mundodolphins.app.viewmodel.SeasonDetailViewModel

private enum class SeasonTab(
    val labelRes: Int,
) {
    Summary(R.string.historical_tab_summary),
    Stats(R.string.historical_tabs_stats),
    Games(R.string.historical_tabs_games),
    Draft(R.string.historical_tabs_draft),
}

@Composable
fun SeasonDetailScreen(
    year: Int,
    onBack: () -> Unit,
    onGameClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SeasonDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable(year) { mutableIntStateOf(0) }

    LaunchedEffect(year) {
        viewModel.loadSeason(year)
    }

    when (val state = uiState) {
        is HistoricalUiState.Error ->
            HistoricalErrorView(
                message = state.message,
                onRetry = { viewModel.loadSeason(year, force = true) },
                modifier = modifier,
            )

        HistoricalUiState.Loading -> HistoricalLoadingView(modifier = modifier)
        is HistoricalUiState.Success -> {
            SeasonDetailContent(
                season = state.data,
                selectedTab = selectedTab,
                onSelectTab = { selectedTab = it },
                onBack = onBack,
                onGameClick = onGameClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SeasonDetailContent(
    season: HistoricalSeasonDetail,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onBack: () -> Unit,
    onGameClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.historical_back_to_season, season.year),
                style = MaterialTheme.typography.bodyMedium,
                color = TealPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onBack),
            )
            Text(
                text = season.title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextDark,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 10.dp),
            )
        }

        SecondaryTabRow(
            selectedTabIndex = selectedTab,
        ) {
            SeasonTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onSelectTab(index) },
                    text = { Text(stringResource(tab.labelRes)) },
                )
            }
        }

        when (SeasonTab.entries[selectedTab]) {
            SeasonTab.Summary -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    item {
                        SeasonSummaryTable(overview = season.overview)
                    }
                }
            }

            SeasonTab.Stats -> {
                if (season.statsSections.isEmpty()) {
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(16.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.historical_stats_empty),
                            color = TextMuted,
                        )
                    }
                } else {
                    SeasonStatsTables(
                        sections = season.statsSections,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            SeasonTab.Games -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items = season.games, key = { it.gameId }) { game ->
                        val resultSummary = rememberGameResultSummary(game)
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .sizeIn(minHeight = 56.dp)
                                    .clickable { onGameClick(season.year, game.gameId) }
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = game.title,
                                    color = TextDark,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = resultSummary,
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                            }
                            Text(
                                text = game.date,
                                color = TextMuted,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        androidx.compose.foundation.layout.Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .background(TableBorder)
                                    .sizeIn(minHeight = 1.dp),
                        )
                    }
                }
            }

            SeasonTab.Draft -> {
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(16.dp),
                ) {
                    if (season.draftPlayers.isEmpty()) {
                        Text(
                            text = stringResource(R.string.historical_draft_empty),
                            color = TextMuted,
                        )
                    } else {
                        DraftTable(players = season.draftPlayers)
                    }
                }
            }
        }
    }
}
