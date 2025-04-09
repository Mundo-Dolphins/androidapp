package es.mundodolphins.app.ui

sealed class Routes(val route: String) {
    data object Feed : Routes("feed")
    data object EpisodeView : Routes("more_information")
    data object UsefulLinks : Routes("useful_links")
    data object SeasonsList : Routes("seasons_list")
    data object SeasonView : Routes("seasons_view")
}