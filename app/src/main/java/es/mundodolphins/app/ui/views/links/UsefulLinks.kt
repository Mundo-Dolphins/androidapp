package es.mundodolphins.app.ui.views.links


import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .shadow(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable { openTab(url, context) }
                ) {
                    Icon(
                        painterResource(R.drawable.link),
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                }
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


@Composable
@Preview(showBackground = true)
fun UsefulLinksScreenPreview() {
    MundoDolphinsTheme {
        UsefulLinksScreen()
    }
}