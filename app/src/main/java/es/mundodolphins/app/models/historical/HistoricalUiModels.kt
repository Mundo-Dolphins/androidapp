package es.mundodolphins.app.models.historical

data class HistoricalSeasonSummary(
    val year: Int,
    val title: String,
)

data class HistoricalSeasonDetail(
    val year: Int,
    val title: String,
    val overview: LinkedHashMap<String, String>,
    val statsSections: List<HistoricalStatsSection>,
    val draftPlayers: List<HistoricalDraftPlayer>,
    val games: List<HistoricalGame>,
)

data class HistoricalStatsSection(
    val title: String,
    val tables: List<HistoricalStatsTable>,
)

data class HistoricalStatsTable(
    val title: String,
    val columns: List<String>,
    val rows: List<LinkedHashMap<String, String>>,
)

data class HistoricalDraftPlayer(
    val columns: LinkedHashMap<String, String>,
) {
    fun valueFor(column: String): String = columns[column].orEmpty()
}

data class HistoricalGame(
    val gameId: String,
    val title: String,
    val date: String,
    val slug: String,
    val linescore: List<HistoricalLineScoreRow>,
    val scoringSummary: List<HistoricalScoringPlay>,
    val gameInfo: LinkedHashMap<String, String>,
)

data class HistoricalLineScoreRow(
    val team: String,
    val periods: LinkedHashMap<String, String>,
    val finalScore: String,
)

data class HistoricalScoringPlay(
    val quarter: String,
    val team: String,
    val description: String,
    val awayScore: String,
    val homeScore: String,
    val time: String,
)
