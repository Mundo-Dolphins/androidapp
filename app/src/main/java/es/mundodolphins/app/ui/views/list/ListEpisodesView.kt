package es.mundodolphins.app.ui.views.list

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.mundodolphins.app.models.Item
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme


@Composable
fun ListEpisodesView(
    episodes: List<Item>,
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

@Preview(showBackground = true)
@Composable
fun ListEpisodesViewPreview() {
    MundoDolphinsTheme {
        ListEpisodesView(parseJsonToItems(), navController = NavController(LocalContext.current))
    }
}

private fun parseJsonToItems(): List<Item> {
    val gson = Gson()
    val type = object : TypeToken<List<Item>>() {}.type
    return gson.fromJson(jsonString, type)
}

private const val jsonString = """
[
    {
      "title":"Carta a los Reyes Magos",
      "pubDate":"2025-01-03 07:43:43",
      "link":"https://www.ivoox.com/carta-a-reyes-magos-audios-mp3_rf_137429858_1.html",
      "guid":"https://www.ivoox.com/137429858",
      "author":"Mundo Dolphins",
      "thumbnail":"",
      "description":"Hugo, Santos y Javi se re\u00fanen en este inicio de a\u00f1o 2025 para escribir a sus majestades de oriente y pedirles una victoria de Miami frente a los Jets y que los Chiefs consigan ganar a los Broncos para que se obre el milagro navide\u00f1o y los Dolphins clasifiquen para postemporada.\r\nAdem\u00e1s comentan los premios otorgados por los Miami Dolphins e intentan aclarar la situacion de Tua Tagovailoa",
      "content":"Hugo, Santos y Javi se re\u00fanen en este inicio de a\u00f1o 2025 para escribir a sus majestades de oriente y pedirles una victoria de Miami frente a los Jets y que los Chiefs consigan ganar a los Broncos para que se obre el milagro navide\u00f1o y los Dolphins clasifiquen para postemporada.\r\nAdem\u00e1s comentan los premios otorgados por los Miami Dolphins e intentan aclarar la situacion de Tua Tagovailoa",
      "enclosure":{
        "link":"https://www.ivoox.com/carta-a-reyes-magos_mf_137429858_feed_1.mp3",
        "type":"audio/mpeg",
        "length":40220064,
        "duration":5027,
        "rating":{
          "scheme":"urn:itunes",
          "value":"no"
        }
      },
      "categories":[
        
      ]
    },
    {
      "title":"Feliz Dolphindad",
      "pubDate":"2024-12-27 10:50:37",
      "link":"https://www.ivoox.com/feliz-dolphindad-audios-mp3_rf_137268336_1.html",
      "guid":"https://www.ivoox.com/137268336",
      "author":"Mundo Dolphins",
      "thumbnail":"",
      "description":"Hugo, Santos y Javi se re\u00fanen en estas fechas navide\u00f1as para hablar de toda la actualidad de los Miami Dolphins . Repasan las tradicionales Phin News que vienen cargadas de premios y reconocimientos para los jugadores de los Dolphins . Analizan la victoria frente a los 49ers e invitan a Santi de la Perrera para realizar una previa del partido frente a los Cleveland Browns",
      "content":"Hugo, Santos y Javi se re\u00fanen en estas fechas navide\u00f1as para hablar de toda la actualidad de los Miami Dolphins . Repasan las tradicionales Phin News que vienen cargadas de premios y reconocimientos para los jugadores de los Dolphins . Analizan la victoria frente a los 49ers e invitan a Santi de la Perrera para realizar una previa del partido frente a los Cleveland Browns",
      "enclosure":{
        "link":"https://www.ivoox.com/feliz-dolphindad_mf_137268336_feed_1.mp3",
        "type":"audio/mpeg",
        "length":43829350,
        "duration":5478,
        "rating":{
          "scheme":"urn:itunes",
          "value":"no"
        }
      },
      "categories":[
        
      ]
    },
    {
      "title":"Houston , Tenemos un problema",
      "pubDate":"2024-12-19 17:48:30",
      "link":"https://www.ivoox.com/houston-tenemos-problema-audios-mp3_rf_137072623_1.html",
      "guid":"https://www.ivoox.com/137072623",
      "author":"Mundo Dolphins",
      "thumbnail":"",
      "description":"Tertulia semanal con el equipo habitual que forman Hugo, Santos y Javi . Analizan todas las novedades de la plantilla del sur de Florida : el corte de Odell Beckham Jr, Skylar y la lesi\u00f3n de DuBose. Desgranan las claves de la dolorosa derrota frente a Texans que deja a los Dolphins al borde de la eliminaci\u00f3n y reciben a un invitado de 49ers Espa\u00f1a para realizar una exhaustiva previa del duelo del pr\u00f3ximo 22 de Diciembre",
      "content":"Tertulia semanal con el equipo habitual que forman Hugo, Santos y Javi . Analizan todas las novedades de la plantilla del sur de Florida : el corte de Odell Beckham Jr, Skylar y la lesi\u00f3n de DuBose. Desgranan las claves de la dolorosa derrota frente a Texans que deja a los Dolphins al borde de la eliminaci\u00f3n y reciben a un invitado de 49ers Espa\u00f1a para realizar una exhaustiva previa del duelo del pr\u00f3ximo 22 de Diciembre",
      "enclosure":{
        "link":"https://www.ivoox.com/houston-tenemos-problema_mf_137072623_feed_1.mp3",
        "type":"audio/mpeg",
        "length":42854670,
        "duration":5356,
        "rating":{
          "scheme":"urn:itunes",
          "value":"no"
        }
      },
      "categories":[
        
      ]
    }
]
"""