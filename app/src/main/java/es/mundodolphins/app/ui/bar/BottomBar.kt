package es.mundodolphins.app.ui.bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme

private val navigationItems = listOf(
    BottomBarButton(
        Routes.Feed.route,
        Icons.Filled.Home,
        R.string.episodios
    ),
    BottomBarButton(
        Routes.UsefulLinks.route,
        Icons.Filled.Search,
        R.string.links
    ),
    BottomBarButton(
        Routes.SeasonsList.route,
        Icons.AutoMirrored.Filled.List,
        R.string.seasons
    )
)

@Composable
fun AppBottomBar(navController: NavHostController) {
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }

    NavigationBar {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = stringResource(item.label))
                },
                label = {
                    Text(
                        stringResource(item.label),
                        color = if (index == selectedNavigationIndex.intValue) Color.Black else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppBottomBarPreview() {
    MundoDolphinsTheme {
        AppBottomBar(NavHostController(LocalContext.current))
    }
}

data class BottomBarButton(val route: String, val icon: ImageVector, val label: Int)