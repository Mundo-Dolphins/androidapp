package es.mundodolphins.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import es.mundodolphins.app.ui.bar.AppBar
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.views.main.MainScreen
import es.mundodolphins.app.viewmodel.RssViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MundoDolphinsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AppBar() }
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
