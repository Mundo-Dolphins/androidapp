package es.mundodolphins.app.ui.views.social

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import es.mundodolphins.app.R
import es.mundodolphins.app.viewmodel.SocialUiModel
import es.mundodolphins.app.viewmodel.SocialViewModel
import es.mundodolphins.app.viewmodel.SocialViewModel.LoadStatus.EMPTY
import es.mundodolphins.app.viewmodel.SocialViewModel.LoadStatus.ERROR
import es.mundodolphins.app.viewmodel.SocialViewModel.LoadStatus.LOADING
import es.mundodolphins.app.viewmodel.SocialViewModel.LoadStatus.SUCCESS

@Composable
fun SocialScreen(
    modifier: Modifier = Modifier,
    model: SocialViewModel = viewModel(),
) {
    val context = LocalContext.current
    val status by model.status.collectAsState()
    val posts by model.posts.collectAsState()

    when (status) {
        SUCCESS -> {
            LazyColumn(
                modifier = modifier.background(color = MaterialTheme.colorScheme.background),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(posts) { post ->
                    SocialPostCard(
                        post = post,
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.postUrl)))
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
                    Text(text = stringResource(R.string.social_error))
                    Button(
                        onClick = { model.fetchSocialPosts() },
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
                Text(text = stringResource(R.string.social_empty))
            }
        }
    }
}

@Composable
private fun SocialPostCard(
    post: SocialUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
        ) {
            Text(
                text = post.profileName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = post.publishedOn,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp),
            )
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            post.imageUrls.take(4).forEach { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.medium),
                )
            }
        }
    }
}
