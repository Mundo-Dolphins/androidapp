package es.mundodolphins.app.ui.historical.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import es.mundodolphins.app.R
import es.mundodolphins.app.models.historical.HistoricalSeasonSummary
import es.mundodolphins.app.ui.theme.TealBorder
import es.mundodolphins.app.ui.theme.TealDeep
import es.mundodolphins.app.ui.theme.TealPale
import es.mundodolphins.app.ui.theme.TextMid
import es.mundodolphins.app.ui.theme.orange

private const val DOLPHINS_LOGO_URL = "https://mundo-dolphins.github.io/logos/miami-dolphins.png"

@Composable
fun SeasonCard(
    season: HistoricalSeasonSummary,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 136.dp)
                .clickable { onClick(season.year) },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, TealBorder),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 136.dp)
                    .background(TealPale.copy(alpha = 0.42f))
                    .padding(14.dp),
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = season.year.toString(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TealDeep,
                )
                Text(
                    text = season.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMid,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(width = 18.dp, height = 3.dp)
                                .background(orange, RoundedCornerShape(8.dp)),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.historical_view_season_cta),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TealDeep,
                    )
                }
            }

            AsyncImage(
                model = DOLPHINS_LOGO_URL,
                contentDescription = null,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(48.dp)
                        .alpha(0.8f),
            )
        }
    }
}
