package es.mundodolphins.app.ui.views.links


import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import es.mundodolphins.app.R

val links = listOf(
    "https://www.mundodolphins.es" to "Mundo Dolphins",
    "https://www.instagram.com/dolfan_club_spain/" to "Dolfan Club España"
)

@Composable
fun UsefulLinksScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        links.forEach { (url, label) ->
            Row {
                Icon(
                    painterResource(R.drawable.link),
                    contentDescription = label,
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable {
                            openTab(url, context)
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}


fun openTab(url: String, context: Context) {
    val builder = CustomTabsIntent.Builder().apply {
        setShowTitle(true)
        setInstantAppsEnabled(true)
    }.build()

    builder.intent.setPackage("com.android.chrome")
    builder.launchUrl(context, url.toUri())
}
