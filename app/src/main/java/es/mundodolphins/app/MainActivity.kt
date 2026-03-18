package es.mundodolphins.app

import android.Manifest
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import es.mundodolphins.app.install.InstallReferrerHelper
import es.mundodolphins.app.notifications.PushNotificationData
import es.mundodolphins.app.observer.ConnectivityObserver
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.bar.AppBar
import es.mundodolphins.app.ui.bar.AppNavigationDrawer
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.views.main.MainScreen
import es.mundodolphins.app.viewmodel.ArticlesViewModel
import es.mundodolphins.app.viewmodel.EpisodesViewModel
import es.mundodolphins.app.viewmodel.SocialViewModel
import es.mundodolphins.app.viewmodel.VideosViewModel
import kotlinx.coroutines.launch

private const val TAG = "MundoDolphinsDeepLink"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var pendingPushTarget by mutableStateOf<PushNotificationData.Target?>(null)
    private var pendingEpisodeId by mutableStateOf<Long?>(null)

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        pendingPushTarget = PushNotificationData.parseTarget(intent)
        logDeepLinkIntent("cold-start", intent)
        if (isDebugBuild()) {
            logManifestResolution("manifest deep-link capability probe")
        }
        requestNotificationPermissionIfNeeded()

        // Parse the incoming deep link (if any) into a pending episode ID before setContent,
        // so the first composition already has the correct navigation target.
        if (pendingPushTarget == null) {
            pendingEpisodeId = parseEpisodeIdFromIntent("cold-start", intent)
        }

        // Query the Play Install Referrer once per install, only when no higher-priority
        // navigation target (App Link or push notification) is already present.
        if (pendingPushTarget == null && pendingEpisodeId == null) {
            lifecycleScope.launch {
                if (!InstallReferrerHelper.isAlreadyProcessed(this@MainActivity)) {
                    val referrerId = InstallReferrerHelper.queryEpisodeId(this@MainActivity)
                    InstallReferrerHelper.markProcessed(this@MainActivity)
                    if (referrerId != null) {
                        Log.i(TAG, "[cold-start] install referrer resolved to episodeId=$referrerId")
                        pendingEpisodeId = referrerId
                    }
                }
            }
        }

        setContent {
            MundoDolphinsTheme {
                MundoDolphinsScreen(
                    pushTarget = pendingPushTarget,
                    onPushTargetHandled = { pendingPushTarget = null },
                    pendingEpisodeId = pendingEpisodeId,
                    onPendingEpisodeHandled = { pendingEpisodeId = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        logDeepLinkIntent("warm-start", intent)
        val parsedTarget = PushNotificationData.parseTarget(intent)
        if (parsedTarget != null) {
            Log.i(TAG, "[warm-start] push notification target=$parsedTarget")
            pendingPushTarget = parsedTarget
            pendingEpisodeId = null
        } else {
            pendingEpisodeId = parseEpisodeIdFromIntent("warm-start", intent)
        }
    }

    private fun parseEpisodeIdFromIntent(
        source: String,
        inputIntent: Intent,
    ): Long? {
        if (inputIntent.data == null) {
            Log.d(TAG, "[$source] no URI in intent, deep-link skipped")
            return null
        }
        val episodeId = Routes.EpisodeView.episodeIdFromUri(inputIntent.data)
        return if (episodeId != null) {
            Log.i(TAG, "[$source] episodeId=$episodeId from uri=${inputIntent.dataString}")
            episodeId
        } else {
            Log.w(TAG, "[$source] URI does not match episode pattern: ${inputIntent.dataString}")
            null
        }
    }

    private fun logDeepLinkIntent(
        source: String,
        inputIntent: Intent?,
    ) {
        Log.i(
            TAG,
            "[$source] incoming intent action=${inputIntent?.action} data=${inputIntent?.dataString}",
        )
    }

    private fun logManifestResolution(contextLabel: String) {
        val testUris =
            listOf(
                "https://mundodolphins.es/app/episode/1769624287000/?appAttempt=1",
                "https://www.mundodolphins.es/app/episode/1769624287000/?appAttempt=1",
            )
        testUris.forEach { uriString ->
            val testIntent =
                Intent(Intent.ACTION_VIEW).apply {
                    data = uriString.toUri()
                }
            val candidates =
                packageManager.queryIntentActivities(
                    testIntent,
                    0,
                )
            val topName =
                packageManager.resolveActivity(testIntent, 0)?.activityInfo?.name
            Log.i(
                TAG,
                "[$contextLabel] uri=$uriString resolveCount=${candidates.size} top=$topName",
            )
            candidates.forEachIndexed { index, resolveInfo ->
                Log.i(
                    TAG,
                    "[$contextLabel] candidate[$index]=${resolveInfo.activityInfo.packageName}/${resolveInfo.activityInfo.name}",
                )
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun isDebugBuild(): Boolean = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MundoDolphinsScreen(
    pushTarget: PushNotificationData.Target? = null,
    onPushTargetHandled: () -> Unit = {},
    pendingEpisodeId: Long? = null,
    onPendingEpisodeHandled: () -> Unit = {},
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
    val articlesViewModel: ArticlesViewModel = hiltViewModel()
    val videosViewModel: VideosViewModel = hiltViewModel()
    val socialViewModel: SocialViewModel = hiltViewModel()
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
            HandleDeepLinkNavigation(
                pendingEpisodeId = pendingEpisodeId,
                onPendingEpisodeHandled = onPendingEpisodeHandled,
                navigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(Routes.Feed.route) { inclusive = false }
                    }
                },
            )
            PrefetchAppContent(
                episodesViewModel = viewModel,
                articlesViewModel = articlesViewModel,
                videosViewModel = videosViewModel,
                socialViewModel = socialViewModel,
            )
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
private fun PrefetchAppContent(
    episodesViewModel: EpisodesViewModel,
    articlesViewModel: ArticlesViewModel,
    videosViewModel: VideosViewModel,
    socialViewModel: SocialViewModel,
) {
    LaunchedEffect(Unit) {
        episodesViewModel.refreshDatabase()
        articlesViewModel.fetchArticles()
        videosViewModel.fetchVideos()
        socialViewModel.fetchSocialPosts()
    }
}

@Composable
private fun HandleDeepLinkNavigation(
    pendingEpisodeId: Long?,
    onPendingEpisodeHandled: () -> Unit,
    navigate: (String) -> Unit,
) {
    LaunchedEffect(pendingEpisodeId) {
        if (pendingEpisodeId == null) return@LaunchedEffect
        val route = "${Routes.EpisodeView.route}/$pendingEpisodeId"
        Log.i(TAG, "compose deep-link navigation: episodeId=$pendingEpisodeId route=$route")
        navigate(route)
        onPendingEpisodeHandled()
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

private fun mapToDrawerRoute(route: String?): String =
    when {
        route == null -> Routes.Feed.route
        route.startsWith(Routes.EpisodeView.route) -> Routes.Feed.route
        route.startsWith(Routes.SeasonView.route) -> Routes.SeasonsList.route
        route.startsWith(Routes.Article.route) -> Routes.Articles.route
        else -> route
    }

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(showBackground = true)
fun MundoDolphinsScreenPreview() {
    MundoDolphinsTheme {
        MundoDolphinsScreen()
    }
}
