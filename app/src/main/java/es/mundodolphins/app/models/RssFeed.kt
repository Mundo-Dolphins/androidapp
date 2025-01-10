package es.mundodolphins.app.models

data class RssFeed(
    val feed: Feed?,
    val items: List<Item>,
    val status: String?
)