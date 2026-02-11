package es.mundodolphins.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import es.mundodolphins.app.client.MundoDolphinsClient
import es.mundodolphins.app.data.AppDatabase
import es.mundodolphins.app.notifications.PushNotificationData
import es.mundodolphins.app.observer.ConnectivityObserver
import es.mundodolphins.app.repository.EpisodeRepository
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.bar.AppBar
import es.mundodolphins.app.ui.bar.AppBottomBar
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.views.main.MainScreen
import es.mundodolphins.app.viewmodel.EpisodesViewModel
import es.mundodolphins.app.viewmodel.EpisodesViewModelFactory

class MainActivity : ComponentActivity() {
    private var pendingPushTarget by mutableStateOf<PushNotificationData.Target?>(null)

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        pendingPushTarget = PushNotificationData.parseTarget(intent)
        requestNotificationPermissionIfNeeded()

        setContent {
            MundoDolphinsTheme {
                MundoDolphinsScreen(
                    pushTarget = pendingPushTarget,
                    onPushTargetHandled = { pendingPushTarget = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingPushTarget = PushNotificationData.parseTarget(intent)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MundoDolphinsScreen(
    pushTarget: PushNotificationData.Target? = null,
    onPushTargetHandled: () -> Unit = {},
) {
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

    // Firebase Remote Config is not available in Compose Previews.
    // We wrap it in a LocalInspectionMode check to avoid IllegalStateException.
    if (!LocalInspectionMode.current) {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            },
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar() },
        bottomBar = { AppBottomBar(navController) },
    ) { innerPadding ->
        LaunchedEffect(pushTarget) {
            when (pushTarget) {
                is PushNotificationData.Target.Episode -> {
                    navController.navigate("${Routes.EpisodeView.route}/${pushTarget.episodeId}") {
                        launchSingleTop = true
                    }
                    onPushTargetHandled()
                }

                is PushNotificationData.Target.Article -> {
                    navController.navigate(Routes.Articles.route) {
                        launchSingleTop = true
                    }
                    navController.navigate("${Routes.Article.route}/${pushTarget.publishedTimestamp}") {
                        launchSingleTop = true
                    }
                    onPushTargetHandled()
                }

                null -> Unit
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
