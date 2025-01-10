package es.mundodolphins.app.ui.views.info

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.mundodolphins.app.R
import es.mundodolphins.app.models.Enclosure
import es.mundodolphins.app.models.Item
import es.mundodolphins.app.models.Rating
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.views.player.AudioPlayerView
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun EpisodeScreen(
    item: Item?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item?.title ?: "",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = item?.pubDate?.let { stringResource(R.string.published_on, it) } ?: "",
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = MaterialTheme.typography.labelSmall.fontWeight,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = item?.enclosure?.duration?.toEpisodeDuration()?.let {
                    stringResource(
                        R.string.duration,
                        it.toHoursPart(),
                        it.toMinutesPart(),
                        it.toSecondsPart()
                    )
                } ?: "",
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = MaterialTheme.typography.labelSmall.fontWeight,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = item?.description ?: "",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            item?.enclosure?.link?.let {
                AudioPlayerView(it)
            }
        }
    }
}

private fun Int.toEpisodeDuration() = Duration.ofSeconds(this.toLong())

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun EpisodeScreenPreview() {
    MundoDolphinsTheme {
        EpisodeScreen(
            item =
            Item(
                title = "Carta a los Reyes Magos",
                author = "Mundo Dolphins",
                categories = emptyList(),
                content = "Hugo, Santos y Javi se re\u00fanen en este inicio de a\u00f1o 2025 para escribir a sus majestades de oriente y pedirles una victoria de Miami frente a los Jets y que los Chiefs consigan ganar a los Broncos para que se obre el milagro navide\u00f1o y los Dolphins clasifiquen para postemporada.\r\nAdem\u00e1s comentan los premios otorgados por los Miami Dolphins e intentan aclarar la situacion de Tua Tagovailoa",
                description = "Hugo, Santos y Javi se re\u00fanen en este inicio de a\u00f1o 2025 para escribir a sus majestades de oriente y pedirles una victoria de Miami frente a los Jets y que los Chiefs consigan ganar a los Broncos para que se obre el milagro navide\u00f1o y los Dolphins clasifiquen para postemporada.\r\nAdem\u00e1s comentan los premios otorgados por los Miami Dolphins e intentan aclarar la situacion de Tua Tagovailoa",
                enclosure = Enclosure(
                    link = "https://www.ivoox.com/carta-a-reyes-magos_mf_137429858_feed_1.mp3",
                    length = 40220064,
                    type = "audio/mpeg",
                    duration = 5027,
                    rating = Rating(
                        scheme = "urn:itunes",
                        value = "no"
                    )
                ),
                guid = "https://www.ivoox.com/137429858",
                link = "https://www.ivoox.com/carta-a-reyes-magos-audios-mp3_rf_137429858_1.html",
                pubDate = "2025-01-03 07:43:43",
                thumbnail = "",
            )
        )

    }
}