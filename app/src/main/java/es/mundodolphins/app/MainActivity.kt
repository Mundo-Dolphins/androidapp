package es.mundodolphins.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import es.mundodolphins.app.data.AppDatabase
import es.mundodolphins.app.repository.EpisodeRepository
import es.mundodolphins.app.ui.bar.AppBar
import es.mundodolphins.app.ui.bar.AppBottomBar
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.views.main.MainScreen
import es.mundodolphins.app.viewmodel.EpisodesViewModel
import es.mundodolphins.app.viewmodel.EpisodesViewModelFactory

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val viewModel: EpisodesViewModel = viewModel(
                factory = EpisodesViewModelFactory(
                    EpisodeRepository(
                        AppDatabase.getDatabase(context = LocalContext.current.applicationContext)
                            .episodeDao()
                    )
                )
            )

            MundoDolphinsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AppBar() },
                    bottomBar = { AppBottomBar(navController) }
                ) { innerPadding ->
                    viewModel.refreshDatabase()
                    MainScreen(
                        model = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        navController = navController
                    )
                }
            }
        }
    }
}
