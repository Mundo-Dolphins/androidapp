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
import es.mundodolphins.app.models.Episode
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.views.player.AudioPlayerView

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun EpisodeScreen(
    episode: Episode?,
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
                text = episode?.title ?: "",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.published_on, episode?.publishedOn ?: ""),
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = MaterialTheme.typography.labelSmall.fontWeight,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.duration, episode?.len ?: ""),
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = MaterialTheme.typography.labelSmall.fontWeight,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = episode?.description ?: "",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            AudioPlayerView(episode?.audio ?: "")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun EpisodeScreenPreview() {
    MundoDolphinsTheme {
        EpisodeScreen(
            episode = Episode(
                dateAndTime = "2025-01-09T16:41:00Z",
                description = "Hugo , Santos y Javi se juntan para analizar el último partido de la temporada de los Miami Dolphins . No hubo milagro ; los Broncos ganaron a los Chiefs y además los Dolphins sucumbieron frente a los Jets en el Met Life Stadium debido a los múltiples errores y turnovers ofensivos.  Esta derrota ha traido una variedad de reacciones que analizan en un especial Phin News: la continuidad de Grier y McDaniel , la situación de Weaver , el caso Hill . Nos espera una postemporada movida en Miami",
                audio = "https://ivoox.com/listen_mn_137612153_1.mp3",
                imgMain = "https://static-1.ivoox.com/canales/f/d/2/7/fd27a1f3dd4a0478e921cace5476381c_XXL.jpg",
                imgMini = "https://static-1.ivoox.com/usuarios/2/6/4/4/5951733314462_XXL.jpg",
                len = "01:22:14",
                link = "https://www.ivoox.com/carbon-reyes-magos-audios-mp3_rf_137612153_1.html",
                title = "Carbón de Reyes Magos"
            )
        )

    }
}