package es.mundodolphins.app.ui.views.articles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import es.mundodolphins.app.models.ArticlesResponse
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import io.noties.markwon.Markwon

@Composable
fun ArticleRow(
    article: ArticlesResponse,
    navController: NavController,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .padding(6.dp)
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
                .clip(shape = RoundedCornerShape(20.dp))
                .shadow(elevation = 2.dp, clip = true),
    ) {
        ArticleHeader(article)
        Text(
            text =
                Markwon
                    .create(LocalContext.current)
                    .toMarkdown(
                        article.content.let {
                            if (it.length > 100) {
                                truncateSentence(it) + "..."
                            } else {
                                it
                            }
                        },
                    ).toString(),
            fontSize = 18.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(6.dp),
        )
        ArticleBottom(article, navController)
    }
}

@Composable
private fun ArticleHeader(article: ArticlesResponse) {
    Column(
        modifier =
            Modifier
                .background(colorScheme.secondaryContainer)
                .fillMaxWidth(),
    ) {
        Text(
            text = article.title,
            fontSize = 24.sp,
            fontWeight = Bold,
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
            color = Color.White,
            textAlign = TextAlign.Left,
        )
        Text(
            text = article.publishedDate,
            fontSize = 14.sp,
            color = Color.White,
            modifier =
                Modifier
                    .fillMaxWidth(0.9f)
                    .padding(10.dp),
            textAlign = TextAlign.Left,
        )
    }
}

@Composable
private fun ColumnScope.ArticleBottom(
    article: ArticlesResponse,
    navController: NavController,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
    ) {
        Spacer(
            modifier = Modifier.fillMaxWidth(0.5f),
        )
        Button(
            modifier = Modifier.padding(bottom = 6.dp),
            onClick = {
                navController.navigate(Routes.Article.route + "/${article.publishedTimestamp}")
            },
        ) {
            Text(
                text = stringResource(R.string.more),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = Bold,
                fontSize = 16.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EpisodeRowPreview() {
    MundoDolphinsTheme {
        Column {
            ArticleRow(
                ArticlesResponse(
                    title = "Quinn Ewers: Una apuesta de futuro para los Miami Dolphins",
                    author = "Javi Martín",
                    publishedDate = "2025-04-28T07:55:00Z",
                    content = "Aunque gran parte del foco mediático del Draft 2025 se centró en la caída de Shedeur Sanders hasta la quinta ronda, el descenso de Quinn Ewers hasta la séptima fue igualmente sorprendente.",
                ),
                NavController(LocalContext.current),
            )
        }
    }
}

fun truncateSentence(
    sentence: String,
    maxLength: Int = 100,
): String {
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
