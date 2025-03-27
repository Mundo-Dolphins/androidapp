package es.mundodolphins.app.ui.bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomBar(navController: NavHostController) {
    BottomAppBar(
        actions = {
            IconButton(
                enabled = currentRoute(navController) != Routes.Feed.route,
                onClick = { navController.navigate(Routes.Feed.route) }
            ) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = stringResource(R.string.episodios)
                )
            }
            IconButton(
                enabled = currentRoute(navController) != Routes.UsefulLinks.route,
                onClick = { navController.navigate(Routes.UsefulLinks.route) }
            ) {
                Icon(
                    painterResource(id = R.drawable.travel_explore),
                    contentDescription = stringResource(R.string.links)
                )
            }
            IconButton(
                enabled = currentRoute(navController) != Routes.SeasonsList.route,
                onClick = { navController.navigate(Routes.SeasonsList.route) }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(R.string.seasons)
                )
            }
        }
    )
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}