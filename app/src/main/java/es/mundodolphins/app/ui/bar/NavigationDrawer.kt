package es.mundodolphins.app.ui.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.theme.TealDeep
import es.mundodolphins.app.ui.theme.TealPale
import es.mundodolphins.app.ui.theme.TextMid

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
            route = Routes.HistoricalSeasons.route,
            icon = Icons.Filled.History,
            label = R.string.historical,
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
        DrawerItem(
            route = Routes.Social.route,
            icon = Icons.Filled.Share,
            label = R.string.social,
        ),
    )

@Composable
fun AppNavigationDrawer(
    selectedRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.background,
    ) {
        Text(
            text = stringResource(R.string.navigation_menu_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
        )

        drawerItems.forEach { item ->
            DrawerRow(
                item = item,
                selected = selectedRoute == item.route,
                onClick = { onItemClick(item.route) },
            )
        }
    }
}

@Composable
private fun DrawerRow(
    item: DrawerItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val itemColor = if (selected) TealDeep else TextMid
    Row(
        modifier =
            Modifier
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (selected) TealPale else Color.Transparent)
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.icon != null) {
            Icon(
                imageVector = item.icon,
                contentDescription = stringResource(item.label),
                tint = itemColor,
            )
        } else {
            Icon(
                painter = painterResource(item.drawable!!),
                contentDescription = stringResource(item.label),
                tint = itemColor,
            )
        }
        Text(
            text = stringResource(item.label),
            color = itemColor,
            fontSize = 20.sp,
            letterSpacing = 0.sp,
            modifier = Modifier.padding(start = 22.dp),
        )
    }
}

private data class DrawerItem(
    val route: String,
    val label: Int,
    val icon: ImageVector? = null,
    val drawable: Int? = null,
)
