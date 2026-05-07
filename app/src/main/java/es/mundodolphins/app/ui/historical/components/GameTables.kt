package es.mundodolphins.app.ui.historical.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
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
    val columns =
        listOf(
            stringResource(R.string.historical_linescore_team_header),
            "1",
            "2",
            "3",
            "4",
            stringResource(R.string.historical_linescore_final_header),
        )
    val firstWidth = 154.dp
    val cellWidth = 52.dp
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
                    style = MaterialTheme.typography.labelMedium,
                    modifier =
                        Modifier
                            .width(width)
                            .background(TealDark)
                            .border(1.dp, TableBorder)
                            .padding(horizontal = 8.dp, vertical = 7.dp),
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
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    modifier =
                        Modifier
                            .width(firstWidth)
                            .background(TealRowHead)
                            .border(1.dp, TableBorder)
                            .padding(horizontal = 8.dp, vertical = 7.dp),
                )

                listOf("1", "2", "3", "4").forEach { quarter ->
                    Text(
                        text = row.periods[quarter].orEmpty().ifBlank { stringResource(R.string.historical_dash) },
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier =
                            Modifier
                                .width(cellWidth)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, TableBorder)
                                .padding(horizontal = 8.dp, vertical = 7.dp),
                    )
                }

                Text(
                    text = row.finalScore.ifBlank { stringResource(R.string.historical_dash) },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier =
                        Modifier
                            .width(cellWidth)
                            .background(TealRowAlt)
                            .border(1.dp, TableBorder)
                            .padding(horizontal = 8.dp, vertical = 7.dp),
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
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, TableBorder),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                    .padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
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
                            .size(28.dp)
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
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = scoreLabel,
                    color = TealDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                )
            }

            Text(
                text = play.description,
                color = TextMuted,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
