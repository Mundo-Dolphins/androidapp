package es.mundodolphins.app.ui.views.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import es.mundodolphins.app.R
import es.mundodolphins.app.models.Enclosure
import es.mundodolphins.app.models.Item
import es.mundodolphins.app.models.Rating
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import java.time.format.DateTimeFormatter

@Composable
fun EpisodeRow(episode: Item, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(6.dp)
            .background(colorScheme.secondaryContainer)
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = episode.title,
            fontSize = 24.sp,
            fontWeight = Bold,
            modifier = Modifier
                .padding(top = 6.dp),
            color = colorScheme.primaryContainer,
            textAlign = TextAlign.Left
        )
        Text(
            text = episode.pubDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            fontSize = 10.sp,
            color = colorScheme.tertiary,
            modifier = Modifier
                .fillMaxWidth(0.9f),
            textAlign = TextAlign.Left
        )
        Text(
            text = episode.description.let { it.substring(0, 100) + "..." },
            fontSize = 14.sp,
            color = colorScheme.primaryContainer,
            textAlign = TextAlign.Justify
        )
        Button(
            modifier = Modifier.padding(bottom = 6.dp),
            onClick = {
                navController.navigate(Routes.EpisodeView.route + "/${episode.id}")
            },
        ) {
            Text(
                text = stringResource(R.string.more)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EpisodeRowPreview() {
    MundoDolphinsTheme {
        EpisodeRow(
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
            ),
            NavController(LocalContext.current)
        )
    }
}
