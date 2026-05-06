package es.mundodolphins.app.ui.historical.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import es.mundodolphins.app.R
import es.mundodolphins.app.models.historical.HistoricalLineScoreRow
import es.mundodolphins.app.models.historical.HistoricalScoringPlay
import es.mundodolphins.app.ui.theme.TableBorder
import es.mundodolphins.app.ui.theme.TealDark
import es.mundodolphins.app.ui.theme.TealHover
import es.mundodolphins.app.ui.theme.TealRowAlt
import es.mundodolphins.app.ui.theme.TealRowHead
import es.mundodolphins.app.ui.theme.TextDark
import es.mundodolphins.app.ui.theme.TextMuted

@Composable
fun LinescoreTable(
    rows: List<HistoricalLineScoreRow>,
    modifier: Modifier = Modifier,
) {
    val columns = listOf(stringResource(R.string.historical_linescore_team_header), "1", "2", "3", "4", stringResource(R.string.historical_linescore_final_header))
    val firstWidth = 180.dp
    val cellWidth = 66.dp
    val tableWidth = firstWidth + (cellWidth * (columns.size - 1))

    Column(
        modifier =
            modifier
                .horizontalScroll(rememberScrollState())
                .border(1.dp, TableBorder),
    ) {
        Row(modifier = Modifier.width(tableWidth)) {
            columns.forEachIndexed { index, label ->
                val width = if (index == 0) firstWidth else cellWidth
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier =
                        Modifier
                            .width(width)
                            .background(TealDark)
                            .border(1.dp, TableBorder)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }

        rows.forEach { row ->
            Row(
                modifier =
                    Modifier
                        .width(tableWidth)
                        .background(TealHover),
            ) {
                Text(
                    text = row.team,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold,
                    modifier =
                        Modifier
                            .width(firstWidth)
                            .background(TealRowHead)
                            .border(1.dp, TableBorder)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                )

                listOf("1", "2", "3", "4").forEach { quarter ->
                    Text(
                        text = row.periods[quarter].orEmpty().ifBlank { stringResource(R.string.historical_dash) },
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .width(cellWidth)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, TableBorder)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }

                Text(
                    text = row.finalScore.ifBlank { stringResource(R.string.historical_dash) },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            .width(cellWidth)
                            .background(TealRowAlt)
                            .border(1.dp, TableBorder)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
fun ScoringPlayItem(
    play: HistoricalScoringPlay,
    modifier: Modifier = Modifier,
) {
    val scoreLabel =
        stringResource(
            R.string.historical_compact_score,
            play.awayScore.ifBlank { stringResource(R.string.historical_dash) },
            play.homeScore.ifBlank { stringResource(R.string.historical_dash) },
        )

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        color = TealDark,
                        size = Size(4.dp.toPx(), size.height),
                    )
                },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(1.dp, TableBorder)
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .size(32.dp)
                            .background(color = TealDark, shape = CircleShape),
                ) {
                    Text(
                        text = stringResource(R.string.historical_quarter_badge, play.quarter),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                if (play.time.isNotBlank()) {
                    Text(
                        text = play.time,
                        color = TextMuted,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                Text(
                    text = play.team,
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = scoreLabel,
                    color = TealDark,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = play.description,
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

