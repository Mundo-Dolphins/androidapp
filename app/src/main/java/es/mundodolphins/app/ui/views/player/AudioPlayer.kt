package es.mundodolphins.app.ui.views.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.viewmodel.player.PlayerViewModel
import kotlinx.coroutines.delay

@Composable
fun AudioPlayerView(
    episodeId: Long,
    mp3Url: String,
    playerViewModel: PlayerViewModel = viewModel(),
) {
    val context = LocalContext.current
    val player by playerViewModel.playerState.collectAsState()

    LaunchedEffect(mp3Url) {
        if (mp3Url.isNotEmpty()) {
            playerViewModel.initializePlayer(context, episodeId, mp3Url)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            playerViewModel.savePlayerState()
            playerViewModel.releasePlayer(context)
        }
    }

    Column {
        PlayerControls(player, playerViewModel)
    }
}

@Composable
private fun PlayerControls(
    player: ExoPlayer?,
    playerViewModel: PlayerViewModel,
) {
    val isPlaying = remember { mutableStateOf(false) }
    val currentPosition = remember { mutableLongStateOf(0) }
    val sliderPosition = remember { mutableLongStateOf(0) }
    val totalDuration = remember { mutableLongStateOf(0) }

    playerViewModel.isPlaying.observeForever { isPlaying.value = it }
    playerViewModel.duration.observeForever { totalDuration.longValue = it }

    LaunchedEffect(key1 = player?.currentPosition, key2 = player?.isPlaying) {
        delay(1000)
        currentPosition.longValue = player?.currentPosition ?: 0
    }

    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
    }

    LaunchedEffect(player?.duration) {
        if (player?.duration != null && player.duration > 0) {
            totalDuration.longValue = player.duration
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
            ) {
                TrackSlider(
                    value = sliderPosition.longValue.toFloat(),
                    onValueChange = {
                        sliderPosition.longValue = it.toLong()
                    },
                    onValueChangeFinished = {
                        currentPosition.longValue = sliderPosition.longValue
                        player?.seekTo(sliderPosition.longValue)
                    },
                    songDuration = totalDuration.longValue.toFloat(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = (currentPosition.longValue).convertToText(),
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(8.dp),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                    )

                    val remainTime = totalDuration.longValue - currentPosition.longValue
                    Text(
                        text = if (remainTime >= 0) remainTime.convertToText() else "",
                        modifier =
                            Modifier
                                .padding(8.dp),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ControlButton(
                    isPlaying = isPlaying.value,
                    size = 80.dp,
                    onClick = {
                        if (isPlaying.value) player?.pause() else player?.play()
                        isPlaying.value = player?.isPlaying == true
                    },
                )
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
    }
}

/**
 * Tracks and visualizes the song playing actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSlider(
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    songDuration: Float,
) {
    Slider(
        value = value,
        onValueChange = { onValueChange(it) },
        onValueChangeFinished = { onValueChangeFinished() },
        valueRange = 0f..songDuration,
        colors =
            SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color.LightGray,
            ),
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = remember { MutableInteractionSource() },
                thumbSize = DpSize(20.dp, 20.dp),
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
            )
        },
    )
}

/***
 * Player control button
 */
@Composable
fun ControlButton(
    isPlaying: Boolean,
    size: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(size)
                .clip(CircleShape)
                .clickable { onClick() }
                .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(if (isPlaying) R.drawable.pause_icon else R.drawable.play_arrow),
            modifier = Modifier.size(size / 1.5f),
            tint = Color.White,
            contentDescription = null,
        )
    }
}

/***
 * Convert the millisecond to String text
 */
private fun Long.convertToText(): String {
    val sec = this / 1000
    val minutes = sec / 60
    val seconds = sec % 60

    val minutesString =
        if (minutes < 10) {
            "0$minutes"
        } else {
            minutes.toString()
        }
    val secondsString =
        if (seconds < 10) {
            "0$seconds"
        } else {
            seconds.toString()
        }
    return "$minutesString:$secondsString"
}

@Composable
@Preview(showBackground = true)
fun AudioPlayerViewPreview() {
    MundoDolphinsTheme {
        AudioPlayerView(
            1234567,
            "https://www.ivoox.com/carta-a-reyes-magos_mf_137429858_feed_1.mp3",
        )
    }
}
