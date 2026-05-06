package es.mundodolphins.app.ui.historical.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.mundodolphins.app.R
import es.mundodolphins.app.models.historical.HistoricalDraftPlayer
import es.mundodolphins.app.models.historical.HistoricalStatsSection
import es.mundodolphins.app.models.historical.HistoricalStatsTable
import es.mundodolphins.app.ui.theme.TableBorder
import es.mundodolphins.app.ui.theme.TealDark
import es.mundodolphins.app.ui.theme.TealPale
import es.mundodolphins.app.ui.theme.TextDark
import es.mundodolphins.app.ui.theme.TextMid

@Composable
fun SeasonSummaryTable(
    overview: LinkedHashMap<String, String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, TableBorder, RoundedCornerShape(8.dp)),
    ) {
        overview.entries.forEachIndexed { index, entry ->
            val background = if (index % 2 == 0) TealPale else MaterialTheme.colorScheme.surface
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(background)
                        .border(1.dp, TableBorder)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = entry.key,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(0.4f),
                )
                Text(
                    text = entry.value.ifBlank { stringResource(R.string.historical_dash) },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.6f),
                )
            }
        }
    }
}

@Composable
fun SeasonStatsTables(
    sections: List<HistoricalStatsSection>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        sections.forEach { section ->
            item(key = section.title) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = section.title,
                        color = TextDark,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    section.tables.forEach { table ->
                        StatsTable(table = table)
                    }
                }
            }
        }
    }
}

@Composable
fun DraftTable(
    players: List<HistoricalDraftPlayer>,
    modifier: Modifier = Modifier,
) {
    val columns =
        listOf(
            stringResource(R.string.historical_draft_col_round),
            stringResource(R.string.historical_draft_col_pick),
            stringResource(R.string.historical_draft_col_position),
            stringResource(R.string.historical_draft_col_name),
            stringResource(R.string.historical_draft_col_university),
        )
    val cellWidth = 132.dp
    val tableWidth = cellWidth * columns.size

    Column(
        modifier =
            modifier
                .horizontalScroll(rememberScrollState())
                .border(1.dp, TableBorder),
    ) {
        Row(modifier = Modifier.width(tableWidth)) {
            columns.forEach { column ->
                Text(
                    text = column,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier =
                        Modifier
                            .width(cellWidth)
                            .background(TealDark)
                            .border(1.dp, TableBorder)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                )
            }
        }

        players.forEachIndexed { index, player ->
            val rowColor = if (index % 2 == 0) MaterialTheme.colorScheme.surface else TealPale
            Row(modifier = Modifier.width(tableWidth)) {
                columns.forEach { column ->
                    Text(
                        text = player.valueFor(column).ifBlank { stringResource(R.string.historical_dash) },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier =
                            Modifier
                                .width(cellWidth)
                                .background(rowColor)
                                .border(1.dp, TableBorder)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsTable(
    table: HistoricalStatsTable,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (table.title.isNotBlank()) {
            Text(
                text = table.title,
                color = TextMid,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        GenericDataTable(
            columns = table.columns,
            rows = table.rows,
        )
    }
}

@Composable
private fun GenericDataTable(
    columns: List<String>,
    rows: List<LinkedHashMap<String, String>>,
    modifier: Modifier = Modifier,
) {
    val cellWidth = 112.dp
    val tableWidth = cellWidth * columns.size

    Column(
        modifier =
            modifier
                .horizontalScroll(rememberScrollState())
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, TableBorder, RoundedCornerShape(8.dp)),
    ) {
        Row(modifier = Modifier.width(tableWidth)) {
            columns.forEach { column ->
                Text(
                    text = column,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier =
                        Modifier
                            .width(cellWidth)
                            .background(TealDark)
                            .border(1.dp, TableBorder)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                )
            }
        }

        rows.forEachIndexed { index, row ->
            val rowColor = if (index % 2 == 0) MaterialTheme.colorScheme.surface else TealPale
            Row(modifier = Modifier.width(tableWidth)) {
                columns.forEach { column ->
                    Text(
                        text = row[column].orEmpty().ifBlank { stringResource(R.string.historical_dash) },
                        style = MaterialTheme.typography.bodySmall,
                        modifier =
                            Modifier
                                .width(cellWidth)
                                .background(rowColor)
                                .border(1.dp, TableBorder)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
    HorizontalDivider(color = TableBorder, modifier = Modifier.padding(top = 12.dp))
}
