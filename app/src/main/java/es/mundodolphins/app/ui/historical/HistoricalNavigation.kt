package es.mundodolphins.app.ui.historical

import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object HistoricalNavigation {
    const val seasonsRoute = "historical"
    const val seasonRoutePattern = "historical/{year}"
    const val gameRoutePattern = "historical/{year}/game/{gameId}"

    fun seasonRoute(year: Int): String = "historical/$year"

    fun gameRoute(
        year: Int,
        gameId: String,
    ): String = "historical/$year/game/${Uri.encode(gameId)}"
}

fun NavGraphBuilder.historicalNavigation(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    composable(route = HistoricalNavigation.seasonsRoute) {
        HistoricalSeasonsScreen(
            onSeasonClick = { year -> navController.navigate(HistoricalNavigation.seasonRoute(year)) },
            modifier = modifier,
        )
    }

    composable(
        route = HistoricalNavigation.seasonRoutePattern,
        arguments = listOf(navArgument("year") { type = NavType.IntType }),
    ) { backStackEntry ->
        val year = backStackEntry.arguments?.getInt("year") ?: 0
        SeasonDetailScreen(
            year = year,
            onBack = { navController.popBackStack() },
            onGameClick = { selectedYear, gameId ->
                navController.navigate(HistoricalNavigation.gameRoute(selectedYear, gameId))
            },
            modifier = modifier,
        )
    }

    composable(
        route = HistoricalNavigation.gameRoutePattern,
        arguments =
            listOf(
                navArgument("year") { type = NavType.IntType },
                navArgument("gameId") { type = NavType.StringType },
            ),
    ) { backStackEntry ->
        val year = backStackEntry.arguments?.getInt("year") ?: 0
        val gameId = backStackEntry.arguments?.getString("gameId").orEmpty()
        GameDetailScreen(
            year = year,
            gameId = gameId,
            onBack = { navController.popBackStack() },
            modifier = modifier,
        )
    }
}
