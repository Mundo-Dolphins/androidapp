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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme

private val navigationItems =
    listOf(
        BottomBarButton(
            Routes.Feed.route,
            icon = Icons.Filled.Home,
            label = R.string.episodios,
        ),
        BottomBarButton(
            Routes.UsefulLinks.route,
            icon = Icons.Filled.Search,
            label = R.string.links,
        ),
        BottomBarButton(
            Routes.SeasonsList.route,
            icon = Icons.AutoMirrored.Filled.List,
            label = R.string.seasons,
        ),
        BottomBarButton(
            Routes.Articles.route,
            drawable = R.drawable.newspaper,
            label = R.string.news,
        ),
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
                    if (item.icon != null) {
                        Icon(imageVector = item.icon, contentDescription = stringResource(item.label))
                    } else {
                        Icon(painter = painterResource(item.drawable!!), contentDescription = stringResource(item.label))
                    }
                },
                label = {
                    Text(
                        stringResource(item.label),
                        color = if (index == selectedNavigationIndex.intValue) Color.Black else Color.Gray,
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.surface,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                    ),
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

data class BottomBarButton(
    val route: String,
    val label: Int,
    val icon: ImageVector? = null,
    val drawable: Int? = null,
)
