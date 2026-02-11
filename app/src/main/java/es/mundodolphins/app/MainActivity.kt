package es.mundodolphins.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import es.mundodolphins.app.notifications.PushNotificationData
import es.mundodolphins.app.observer.ConnectivityObserver
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.bar.AppBar
import es.mundodolphins.app.ui.bar.AppNavigationDrawer
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.views.main.MainScreen
import es.mundodolphins.app.viewmodel.EpisodesViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri
import kotlinx.coroutines.launch

@AndroidEntryPoint
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { AppBar() },
        ) { innerPadding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Mundo Dolphins Preview")
            }
        }
        return
    }

    val viewModel: EpisodesViewModel = hiltViewModel()
    val context = LocalContext.current
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val isConnected by connectivityObserver.isConnected.observeAsState(initial = true)

    var latestVersionCode by remember { mutableLongStateOf(0L) }
    val currentVersionCode =
        context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode

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
        LaunchedEffect(Unit) {
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    latestVersionCode = remoteConfig.getLong("latest_version_code")
                }
            }
        }
    }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedRoute = mapToDrawerRoute(currentBackStackEntry?.destination?.route)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawer(
                selectedRoute = selectedRoute,
                onItemClick = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                    drawerScope.launch { drawerState.close() }
                },
            )
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AppBar(
                    onMenuClick = { drawerScope.launch { drawerState.open() } },
                )
            },
        ) { innerPadding ->
            HandlePushNavigation(
                pushTarget = pushTarget,
                onPushTargetHandled = onPushTargetHandled,
                navigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
            )

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
                                .background(color = MaterialTheme.colorScheme.error)
                                .statusBarsPadding(),
                    ) {
                        Text(
                            text = "No internet connection",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onError,
                        )
                    }
                }

                UpdateAvailableBanner(
                    latestVersionCode = latestVersionCode,
                    currentVersionCode = currentVersionCode,
                    onClick = {
                        val intent =
                            Intent(Intent.ACTION_VIEW).apply {
                                data = "market://details?id=${context.packageName}".toUri()
                                setPackage("com.android.vending")
                            }
                        context.startActivity(intent)
                    },
                )

                MainScreen(
                    episodesViewModel = viewModel,
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun HandlePushNavigation(
    pushTarget: PushNotificationData.Target?,
    onPushTargetHandled: () -> Unit,
    navigate: (String) -> Unit,
) {
    LaunchedEffect(pushTarget) {
        when (pushTarget) {
            is PushNotificationData.Target.Episode -> {
                navigate("${Routes.EpisodeView.route}/${pushTarget.episodeId}")
                onPushTargetHandled()
            }

            is PushNotificationData.Target.Article -> {
                navigate(Routes.Articles.route)
                navigate("${Routes.Article.route}/${pushTarget.publishedTimestamp}")
                onPushTargetHandled()
            }

            null -> Unit
        }
    }
}

@Composable
private fun UpdateAvailableBanner(
    latestVersionCode: Long,
    currentVersionCode: Long,
    onClick: () -> Unit,
) {
    if (latestVersionCode <= currentVersionCode) return
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                .clickable(onClick = onClick)
                .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Text(
                text = "¡Nueva versión disponible! Toca para actualizar.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

private fun mapToDrawerRoute(route: String?): String {
    return when {
        route == null -> Routes.Feed.route
        route.startsWith(Routes.EpisodeView.route) -> Routes.Feed.route
        route.startsWith(Routes.SeasonView.route) -> Routes.SeasonsList.route
        route.startsWith(Routes.Article.route) -> Routes.Articles.route
        else -> route
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
