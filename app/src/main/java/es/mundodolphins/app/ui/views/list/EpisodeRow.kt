package es.mundodolphins.app.ui.views.list

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.time.Instant

@Composable
fun EpisodeRow(episode: Episode, navController: NavController) {
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
                .padding(top = 6.dp, start = 6.dp, end = 6.dp),
            color = Color.White,
            textAlign = TextAlign.Left
        )
        Row {
            Text(
                text = episode.publishedOn,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                textAlign = TextAlign.Left,
                fontWeight = Bold
            )
            if (episode.listeningStatus == LISTENED) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = stringResource(R.string.listened),
                    tint = Color.White
                )
            } else if (episode.listeningStatus == LISTENING) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = stringResource(R.string.listening),
                    tint = Color.White
                )
            }
        }
        Text(
            text = Html.fromHtml(
                episode.description.let {
                    if (it.length > 100)
                        it.substring(0, 100) + "..."
                    else it
                },
                Html.FROM_HTML_MODE_LEGACY
            ).toString(),
            fontSize = 18.sp,
            color = Color.White,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(6.dp)
        )
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


@Preview(showBackground = true)
@Composable
fun EpisodeRowPreview() {
    MundoDolphinsTheme {
        EpisodeRow(
            Episode(
                id = 1L,
                title = "Mundo Dolphins Podcast",
                description = "En este episodio escuchamos noticias sobre los Miami Dolphins",
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
    }
}