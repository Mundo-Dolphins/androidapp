package es.mundodolphins.app.ui.views.list

import android.text.Html
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
import es.mundodolphins.app.models.Episode
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme

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
        Text(
            text = episode.publishedOn,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.9f),
            textAlign = TextAlign.Left,
            fontWeight = Bold
        )
        Text(
            text = Html.fromHtml(episode.description.let { it.substring(0, 100) + "..." }, Html.FROM_HTML_MODE_LEGACY).toString(),
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
                dateAndTime = "1736440860",
                description = "Hugo , Santos y Javi se juntan para analizar el último partido de la temporada de los Miami Dolphins . No hubo milagro ; los Broncos ganaron a los Chiefs y además los Dolphins sucumbieron frente a los Jets en el Met Life Stadium debido a los múltiples errores y turnovers ofensivos.  Esta derrota ha traido una variedad de reacciones que analizan en un especial Phin News: la continuidad de Grier y McDaniel , la situación de Weaver , el caso Hill . Nos espera una postemporada movida en Miami",
                audio = "http://ivoox.com/listen_mn_137612153_1.mp3",
                imgMain = "https://static-1.ivoox.com/canales/f/d/2/7/fd27a1f3dd4a0478e921cace5476381c_XXL.jpg",
                imgMini = "https://static-1.ivoox.com/usuarios/2/6/4/4/5951733314462_XXL.jpg",
                len = "01:22:14",
                link = "https://www.ivoox.com/carbon-reyes-magos-audios-mp3_rf_137612153_1.html",
                title = "Carbón de Reyes Magos"
            ),
            NavController(LocalContext.current)
        )
    }
}
