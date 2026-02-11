package es.mundodolphins.app.ui.views.articles

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.mundodolphins.app.R
import es.mundodolphins.app.models.ArticlesResponse
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import io.noties.markwon.Markwon
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ArticleScreen(
    article: ArticlesResponse?,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var bodyTextScale by rememberSaveable { mutableFloatStateOf(1f) }

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ArticleHeader(navController, article)
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                ArticleInfo(article)
                TextSizeControls(
                    onDecrease = { bodyTextScale = (bodyTextScale - 0.1f).coerceIn(0.8f, 1.8f) },
                    onIncrease = { bodyTextScale = (bodyTextScale + 0.1f).coerceIn(0.8f, 1.8f) },
                )
                Spacer(
                    modifier =
                        Modifier
                            .padding(16.dp)
                            .height(1.dp),
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                ) {
                    Text(
                        text =
                            article?.content?.let {
                                Markwon.create(LocalContext.current).toMarkdown(it).toString()
                            } ?: "",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * bodyTextScale,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                        textAlign = TextAlign.Justify,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TextSizeControls(
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        OutlinedButton(onClick = onDecrease) {
            Text(text = stringResource(R.string.text_size_decrease))
        }
        OutlinedButton(
            onClick = onIncrease,
            modifier = Modifier.padding(start = 8.dp),
        ) {
            Text(text = stringResource(R.string.text_size_increase))
        }
    }
}

@Composable
private fun ArticleHeader(
    navController: NavController,
    article: ArticlesResponse?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
        Text(
            text = article?.title ?: "",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ArticleInfo(article: ArticlesResponse?) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.published_on, article?.publishedDate ?: ""),
            fontSize = MaterialTheme.typography.labelLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.author, article?.author ?: ""),
            fontSize = MaterialTheme.typography.labelLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun ArticleScreenPreview() {
    MundoDolphinsTheme {
        ArticleScreen(
            article =
                ArticlesResponse(
                    title = "Sample Article",
                    content = "This is a sample article content",
                    publishedDate = Instant.now().toString(),
                    author = "John Doe",
                ),
            navController = NavController(LocalContext.current),
        )
    }
}
