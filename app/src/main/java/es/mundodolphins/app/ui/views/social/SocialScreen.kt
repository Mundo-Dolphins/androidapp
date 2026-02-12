package es.mundodolphins.app.ui.views.social

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
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
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BlueSkyPostWebView(
                postUrl = post.postUrl,
                preferredHeight = post.preferredEmbedHeight(),
            )
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = post.profileName.ifBlank { stringResource(R.string.social) })
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun BlueSkyPostWebView(
    postUrl: String,
    preferredHeight: androidx.compose.ui.unit.Dp,
) {
    val context = LocalContext.current

    AndroidView(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(preferredHeight)
                .clip(RoundedCornerShape(12.dp)),
        factory = { viewContext ->
            WebView(viewContext).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
                isVerticalScrollBarEnabled = true
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                    allowFileAccess = false
                }
                webViewClient =
                    object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?,
                        ): Boolean {
                            val uri = request?.url ?: return false
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            return true
                        }
                    }
                loadUrl(postUrl)
            }
        },
        update = { view ->
            if (view.url != postUrl) {
                view.loadUrl(postUrl)
            }
        },
    )
}

private fun SocialUiModel.preferredEmbedHeight(): androidx.compose.ui.unit.Dp {
    return when {
        imageUrls.isNotEmpty() -> 560.dp
        description.length > 220 -> 480.dp
        else -> 380.dp
    }
}
