package es.mundodolphins.app.ui.views.videos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import es.mundodolphins.app.R
import es.mundodolphins.app.viewmodel.VideoUiModel
import es.mundodolphins.app.viewmodel.VideosViewModel
import es.mundodolphins.app.viewmodel.VideosViewModel.LoadStatus.EMPTY
import es.mundodolphins.app.viewmodel.VideosViewModel.LoadStatus.ERROR
import es.mundodolphins.app.viewmodel.VideosViewModel.LoadStatus.LOADING
import es.mundodolphins.app.viewmodel.VideosViewModel.LoadStatus.SUCCESS

@Composable
fun VideosScreen(
    modifier: Modifier = Modifier,
    model: VideosViewModel = viewModel(),
) {
    val context = LocalContext.current
    val status by model.status.collectAsState()
    val videos by model.videos.collectAsState()

    when (status) {
        SUCCESS -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 180.dp),
                modifier = modifier.background(color = colorScheme.background),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(videos) { video ->
                    VideoCard(
                        video = video,
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(video.url)))
                        },
                    )
                }
            }
        }

        LOADING -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize(0.5F))
            }
        }

        ERROR -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(R.string.videos_error))
                    Button(
                        onClick = { model.fetchVideos(force = true) },
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        Text(text = stringResource(R.string.retry))
                    }
                }
            }
        }

        EMPTY -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(text = stringResource(R.string.videos_empty))
            }
        }
    }
}

@Composable
private fun VideoCard(
    video: VideoUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        colors =
            CardDefaults.cardColors(
                containerColor = colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            var useFallbackThumbnail by remember(video.thumbnailUrl) { mutableStateOf(false) }
            AsyncImage(
                model = if (useFallbackThumbnail) video.thumbnailFallbackUrl else video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                onError = {
                    if (!useFallbackThumbnail && video.thumbnailFallbackUrl != null) {
                        useFallbackThumbnail = true
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
            )
            Text(
                text = video.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(top = 8.dp),
            )
            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (!video.duration.isNullOrBlank()) {
                    Text(
                        text = video.duration,
                        color = colorScheme.onSecondaryContainer,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    )
                }
                Text(
                    text = video.publishedOn,
                    color = colorScheme.onSecondaryContainer,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
