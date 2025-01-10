package es.mundodolphins.app.ui

sealed class Routes(val route: String) {
    data object EpisodesList: Routes("list")
    data object EpisodeView: Routes("more_information")
}