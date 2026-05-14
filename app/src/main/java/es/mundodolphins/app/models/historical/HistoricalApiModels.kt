package es.mundodolphins.app.models.historical

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class HistoricalSeasonsResponse(
    @SerializedName("seasons")
    val seasons: List<HistoricalSeasonSummaryResponse> = emptyList(),
)

data class HistoricalSeasonSummaryResponse(
    @SerializedName("year")
    val year: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("urls")
    val urls: HistoricalSeasonUrlsResponse = HistoricalSeasonUrlsResponse(),
)

data class HistoricalSeasonUrlsResponse(
    @SerializedName("season")
    val season: String = "",
    @SerializedName("stats")
    val stats: String = "",
    @SerializedName("games")
    val games: String = "",
)

data class HistoricalSeasonResponse(
    @SerializedName("sections")
    val sections: Map<String, JsonElement> = emptyMap(),
)

data class HistoricalSeasonStatsResponse(
    @SerializedName("sections")
    val sections: Map<String, JsonElement> = emptyMap(),
    @SerializedName("title")
    val title: String = "",
    @SerializedName("year")
    val year: Int = 0,
)

data class HistoricalGamesResponse(
    @SerializedName("games")
    val games: List<HistoricalGameResponse> = emptyList(),
)

data class HistoricalGameResponse(
    @SerializedName("game_id")
    val gameId: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("sections")
    val sections: Map<String, JsonElement> = emptyMap(),
)
