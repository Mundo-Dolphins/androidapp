package es.mundodolphins.app.ui.views.list

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import es.mundodolphins.app.data.Episode


@Composable
fun ListEpisodesView(
    episodes: List<Episode>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.background(color = colorScheme.background)
    ) {
        items(episodes) {
            EpisodeRow(it, navController)
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun ListEpisodesViewPreview() {
    MundoDolphinsTheme {
        ListEpisodesView(parseJsonToItems(), navController = NavController(LocalContext.current))
    }
}

private fun parseJsonToItems() =
    Gson().fromJson<List<EpisodeResponse>>(jsonString, object : TypeToken<List<EpisodeResponse>>() {}.type)

private const val jsonString = """
[
  {
    "date": "1736440860",
    "description": "Hugo , Santos y Javi se juntan para analizar el último partido de la temporada de los Miami Dolphins . No hubo milagro ; los Broncos ganaron a los Chiefs y además los Dolphins sucumbieron frente a los Jets en el Met Life Stadium debido a los múltiples errores y turnovers ofensivos.  Esta derrota ha traido una variedad de reacciones que analizan en un especial Phin News: la continuidad de Grier y McDaniel , la situación de Weaver , el caso Hill . Nos espera una postemporada movida en Miami",
    "audio": "http://ivoox.com/listen_mn_137612153_1.mp3",
    "imgMain": "https://static-1.ivoox.com/canales/f/d/2/7/fd27a1f3dd4a0478e921cace5476381c_XXL.jpg",
    "imgMini": "https://static-1.ivoox.com/usuarios/2/6/4/4/5951733314462_XXL.jpg",
    "len": "01:22:14",
    "link": "https://www.ivoox.com/carbon-reyes-magos-audios-mp3_rf_137612153_1.html",
    "title": "Carbón de Reyes Magos"
  },
  {
    "date": "1735893780",
    "description": "Hugo, Santos y Javi se reúnen en este inicio de año 2025 para escribir a sus majestades de oriente y pedirles una victoria de Miami frente a los Jets y que los Chiefs consigan ganar a los Broncos para que se obre el milagro navideño y los Dolphins clasifiquen para postemporada.\nAdemás comentan los premios otorgados por los Miami Dolphins e intentan aclarar la situacion de Tua Tagovailoa",
    "audio": "http://ivoox.com/listen_mn_137429858_1.mp3",
    "imgMain": "https://static-1.ivoox.com/canales/f/d/2/7/fd27a1f3dd4a0478e921cace5476381c_XXL.jpg",
    "imgMini": "https://static-1.ivoox.com/usuarios/2/6/4/4/5951733314462_XXL.jpg",
    "len": "01:23:47",
    "link": "https://www.ivoox.com/carta-a-reyes-magos-audios-mp3_rf_137429858_1.html",
    "title": "Carta a los Reyes Magos"
  }
]
"""
 */