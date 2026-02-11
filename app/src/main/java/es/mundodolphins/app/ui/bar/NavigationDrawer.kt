package es.mundodolphins.app.ui.bar

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.Routes

private val drawerItems =
    listOf(
        DrawerItem(
            route = Routes.Feed.route,
            icon = Icons.Filled.Home,
            label = R.string.episodios,
        ),
        DrawerItem(
            route = Routes.UsefulLinks.route,
            icon = Icons.Filled.Search,
            label = R.string.links,
        ),
        DrawerItem(
            route = Routes.SeasonsList.route,
            icon = Icons.AutoMirrored.Filled.List,
            label = R.string.seasons,
        ),
        DrawerItem(
            route = Routes.Articles.route,
            drawable = R.drawable.newspaper,
            label = R.string.news,
        ),
        DrawerItem(
            route = Routes.Videos.route,
            drawable = R.drawable.play_arrow,
            label = R.string.videos,
        ),
    )

@Composable
fun AppNavigationDrawer(
    selectedRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(modifier = modifier) {
        Text(
            text = stringResource(R.string.navigation_menu_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
        )

        drawerItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(text = stringResource(item.label)) },
                selected = selectedRoute == item.route,
                onClick = { onItemClick(item.route) },
                icon = {
                    if (item.icon != null) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.label),
                        )
                    } else {
                        Icon(
                            painter = painterResource(item.drawable!!),
                            contentDescription = stringResource(item.label),
                        )
                    }
                },
                colors =
                    NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
    }
}

private data class DrawerItem(
    val route: String,
    val label: Int,
    val icon: ImageVector? = null,
    val drawable: Int? = null,
)
