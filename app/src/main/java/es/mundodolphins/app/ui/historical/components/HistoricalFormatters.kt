package es.mundodolphins.app.ui.historical.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.mundodolphins.app.R
import es.mundodolphins.app.models.historical.HistoricalGame

@Composable
fun rememberGameResultSummary(game: HistoricalGame): String {
    val fallback = stringResource(R.string.historical_result_unavailable)
    val separator = stringResource(R.string.historical_result_separator)
    val dash = stringResource(R.string.historical_dash)

    val parts =
        game.linescore
            .filter { it.team.isNotBlank() }
            .mapNotNull { row ->
                val score = row.finalScore.ifBlank { dash }
                if (row.team.isBlank() && score == dash) {
                    null
                } else {
                    stringResource(R.string.historical_result_team_score, row.team, score)
                }
            }

    return if (parts.isEmpty()) fallback else parts.joinToString(separator = separator)
}

