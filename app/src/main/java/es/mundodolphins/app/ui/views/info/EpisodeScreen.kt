package es.mundodolphins.app.ui.views.info

import android.os.Build
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.mundodolphins.app.R
import es.mundodolphins.app.data.AppDatabase
import es.mundodolphins.app.data.Episode
import es.mundodolphins.app.data.Episode.ListeningStatus.NOT_LISTENED
import es.mundodolphins.app.repository.EpisodeRepository
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.views.player.AudioPlayerView
import es.mundodolphins.app.viewmodel.PlayerViewModel
import es.mundodolphins.app.viewmodel.PlayerViewModelFactory
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun EpisodeScreen(
    episode: Episode?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val playerViewModel: PlayerViewModel = viewModel(
        factory = PlayerViewModelFactory(
            EpisodeRepository(
                AppDatabase.getDatabase(context = LocalContext.current.applicationContext)
                    .episodeDao()
            )
        )
    )

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            EpisodeHeader(navController, episode)
            Spacer(
                modifier = Modifier
                    .padding(16.dp)
                    .height(2.dp)
            )
            EpisodeInfo(episode)
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text(
                    text = Html.fromHtml(episode?.description ?: "", Html.FROM_HTML_MODE_COMPACT)
                        .toString(),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = Color.DarkGray,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }

            AudioPlayerView(episode?.id ?: 0, episode?.audio ?: "", playerViewModel)
        }
    }
}

@Composable
private fun EpisodeHeader(
    navController: NavController,
    episode: Episode?
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { navController.popBackStack() }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Text(
            text = episode?.title ?: "",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EpisodeInfo(episode: Episode?) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = stringResource(R.string.published_on, episode?.publishedOn ?: ""),
            fontSize = MaterialTheme.typography.labelLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.duration, episode?.len ?: ""),
            fontSize = MaterialTheme.typography.labelLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun EpisodeScreenPreview() {
    MundoDolphinsTheme {
        EpisodeScreen(
            episode = Episode(
                id = 1,
                title = "Sorpresa y Análisis de la Agencia Libre",
                description = "¡Estamos de vuelta!, El equipo habitual de Mundo Dolphins: Hugo, Javi y Santos se reúne para contaros una sorpresa sobre el poryecto del podcast que nos hace mucha ilusión. Además analizan pormenorizadamente todos los cambios que ha experimentado el roster de los Miami Dolphins y lo que pueden aportar los fichajes de la Agencia libre del conjunto del sur de Florida. Para finalizar aportan unas pequeñas claves de lo que podría esperarse en el próximo draft.",
                audio = "https://www.ivoox.com/episodio1.mp3",
                published = Instant.ofEpochMilli(1744098194000),
                imgMain = "https://www.mundodolphins.es/img.jpg",
                imgMini = "https://www.mundodolphins.es/img.jpg",
                len = "01:29:11",
                link = "https://wwww.mundodolphins.es/episodio.html",
                season = 8,
                listenedProgress = 0L,
                listeningStatus = NOT_LISTENED
            ),
            navController = NavController(LocalContext.current)
        )
    }
}
