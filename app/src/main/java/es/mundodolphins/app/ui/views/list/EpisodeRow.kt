package es.mundodolphins.app.ui.views.list

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import es.mundodolphins.app.R
import es.mundodolphins.app.data.Episode
import es.mundodolphins.app.data.Episode.ListeningStatus.LISTENED
import es.mundodolphins.app.data.Episode.ListeningStatus.LISTENING
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.theme.darkGreen
import es.mundodolphins.app.ui.theme.darkYellow
import es.mundodolphins.app.ui.theme.lightGreen
import es.mundodolphins.app.ui.theme.lightYellow
import java.time.Instant

@Composable
fun EpisodeRow(episode: Episode, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
            .clip(shape = RoundedCornerShape(20.dp))
            .shadow(elevation = 2.dp, clip = true)
    ) {
        EpisodeHeader(episode)
        Text(
            text = Html.fromHtml(
                episode.description.let {
                    if (it.length > 100)
                        truncateSentence(it) + "..."
                    else it
                },
                Html.FROM_HTML_MODE_LEGACY
            ).toString(),
            fontSize = 18.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(6.dp)
        )
        EpisodeBottom(episode, navController)
    }
}

@Composable
private fun EpisodeHeader(episode: Episode) {
    Column(
        modifier = Modifier
            .background(colorScheme.secondaryContainer)
            .fillMaxWidth()
    ) {
        Text(
            text = episode.title,
            fontSize = 24.sp,
            fontWeight = Bold,
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
            color = Color.White,
            textAlign = TextAlign.Left
        )
        Text(
            text = episode.publishedOn,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(10.dp),
            textAlign = TextAlign.Left
        )
    }
}

@Composable
private fun ColumnScope.EpisodeBottom(
    episode: Episode,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(10.dp)
        ) {
            if (episode.listeningStatus == LISTENED) {
                ListenedBadge()
            } else if (episode.listeningStatus == LISTENING) {
                ListeningBadge()
            }
        }
        Button(
            modifier = Modifier.padding(bottom = 6.dp),
            onClick = {
                navController.navigate(Routes.EpisodeView.route + "/${episode.id}")
            },
        ) {
            Text(
                text = stringResource(R.string.more),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ListeningBadge() {
    Row(
        modifier = Modifier
            .background(lightYellow)
            .padding(4.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.pause_icon),
            contentDescription = stringResource(R.string.listening),
            tint = darkYellow,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = stringResource(R.string.listening),
            color = darkYellow,
            modifier = Modifier.padding(start = 4.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ListenedBadge() {
    Row(
        modifier = Modifier
            .background(lightGreen)
            .padding(4.dp)
    ) {
        Icon(
            Icons.Filled.Check,
            contentDescription = stringResource(R.string.listened),
            tint = darkGreen,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = stringResource(R.string.listened),
            color = darkGreen,
            modifier = Modifier.padding(start = 4.dp),
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EpisodeRowPreview() {
    MundoDolphinsTheme {
        Column {
            EpisodeRow(
                Episode(
                    id = 1L,
                    title = "Sorpresa y Análisis de la agencia libre",
                    description = "¡Estamos de vuelta!, El equipo habitual de Mundo Dolphins: Hugo, Javi y Santos se reúne para contaros una sorpresa sobre el poryecto del podcast que nos hace mucha ilusión. Además analizan pormenorizadamente todos los cambios que ha experimentado el roster de los Miami Dolphins y lo que pueden aportar los fichajes de la Agencia libre del conjunto del sur de Florida. Para finalizar aportan unas pequeñas claves de lo que podría esperarse en el próximo draft.",
                    audio = "https://wwww.ivoox.com/audio.mp3",
                    published = Instant.ofEpochMilli(1744038703000),
                    imgMain = "https://www.mundolphins.es/img.jpg",
                    imgMini = "https://www.mundodolphins.es/img.jpg",
                    len = "01:12:23",
                    link = "https://www.ivoox.com/episodio.html",
                    season = 3,
                    listenedProgress = 100,
                    listeningStatus = LISTENING,
                ),
                NavController(LocalContext.current)
            )
            EpisodeRow(
                Episode(
                    id = 1L,
                    title = "Sorpresa y Análisis de la agencia libre",
                    description = "¡Estamos de vuelta!, El equipo habitual de Mundo Dolphins: Hugo, Javi y Santos se reúne para contaros una sorpresa sobre el poryecto del podcast que nos hace mucha ilusión. Además analizan pormenorizadamente todos los cambios que ha experimentado el roster de los Miami Dolphins y lo que pueden aportar los fichajes de la Agencia libre del conjunto del sur de Florida. Para finalizar aportan unas pequeñas claves de lo que podría esperarse en el próximo draft.",
                    audio = "https://wwww.ivoox.com/audio.mp3",
                    published = Instant.ofEpochMilli(1744038703000),
                    imgMain = "https://www.mundolphins.es/img.jpg",
                    imgMini = "https://www.mundodolphins.es/img.jpg",
                    len = "01:12:23",
                    link = "https://www.ivoox.com/episodio.html",
                    season = 3,
                    listenedProgress = 100,
                    listeningStatus = LISTENED,
                ),
                NavController(LocalContext.current)
            )
        }
    }
}

fun truncateSentence(sentence: String, maxLength: Int = 100): String {
    if (sentence.length <= maxLength) {
        return sentence
    }

    var truncatedSentence = ""
    for (word in sentence.split(" ")) {
        if ((truncatedSentence + word).length > maxLength) {
            break
        }
        truncatedSentence += if (truncatedSentence.isEmpty()) word else " $word"
    }

    return truncatedSentence
}