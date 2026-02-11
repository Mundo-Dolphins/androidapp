package es.mundodolphins.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.core.view.WindowCompat
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import es.mundodolphins.app.client.MundoDolphinsClient
import es.mundodolphins.app.data.AppDatabase
import es.mundodolphins.app.observer.ConnectivityObserver
import es.mundodolphins.app.repository.EpisodeRepository
import es.mundodolphins.app.ui.bar.AppBar
import es.mundodolphins.app.ui.bar.AppBottomBar
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.views.main.MainScreen
import es.mundodolphins.app.viewmodel.EpisodesViewModel
import es.mundodolphins.app.viewmodel.EpisodesViewModelFactory
import es.mundodolphins.app.services.AudioPlayerService

class MainActivity : ComponentActivity() {
    private var targetEpisodeId by mutableStateOf<Long?>(null)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        targetEpisodeId = extractEpisodeId(intent)

        setContent {
            MundoDolphinsTheme {
                MundoDolphinsScreen(targetEpisodeId = targetEpisodeId)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        targetEpisodeId = extractEpisodeId(intent)
    }

    private fun extractEpisodeId(intent: android.content.Intent?): Long? {
        val episodeId = intent?.getLongExtra(AudioPlayerService.EXTRA_EPISODE_ID, -1L) ?: -1L
        return if (episodeId > 0L) episodeId else null
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MundoDolphinsScreen(targetEpisodeId: Long? = null) {
    val navController = rememberNavController()
    val viewModel: EpisodesViewModel =
        viewModel(
            factory =
                EpisodesViewModelFactory(
                    EpisodeRepository(
                        AppDatabase
                            .getDatabase(context = LocalContext.current.applicationContext)
                            .episodeDao(),
                    ),
                    MundoDolphinsClient.feedService,
                ),
        )
    val context = LocalContext.current
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val isConnected by connectivityObserver.isConnected.observeAsState(initial = true)
    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    remoteConfig.setConfigSettingsAsync(
        remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        },
    )
    remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar() },
        bottomBar = { AppBottomBar(navController) },
    ) { innerPadding ->
        LaunchedEffect(targetEpisodeId) {
            if (targetEpisodeId != null) {
                navController.navigate("${Routes.EpisodeView.route}/$targetEpisodeId")
            }
        }

        viewModel.refreshDatabase()
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (!isConnected) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(color = Color.Red)
                            .statusBarsPadding(),
                ) {
                    Text(
                        text = "No internet connection",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Yellow,
                    )
                }
            }
            MainScreen(
                episodesViewModel = viewModel,
                modifier = Modifier.padding(innerPadding),
                navController = navController,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(showBackground = true)
fun MundoDolphinsScreenPreview() {
    MundoDolphinsTheme {
        MundoDolphinsScreen()
    }
}
