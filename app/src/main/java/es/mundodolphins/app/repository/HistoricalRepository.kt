package es.mundodolphins.app.repository

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import es.mundodolphins.app.client.HistoricalService
import es.mundodolphins.app.models.historical.HistoricalDraftPlayer
import es.mundodolphins.app.models.historical.HistoricalGame
import es.mundodolphins.app.models.historical.HistoricalGameResponse
import es.mundodolphins.app.models.historical.HistoricalLineScoreRow
import es.mundodolphins.app.models.historical.HistoricalScoringPlay
import es.mundodolphins.app.models.historical.HistoricalSeasonDetail
import es.mundodolphins.app.models.historical.HistoricalSeasonSummary
import es.mundodolphins.app.models.historical.HistoricalSeasonSummaryResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonUrls
import es.mundodolphins.app.models.historical.HistoricalStatsSection
import es.mundodolphins.app.models.historical.HistoricalStatsTable
import retrofit2.HttpException
import retrofit2.Response
import java.text.Normalizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("TooManyFunctions")
class HistoricalRepository
    @Inject
    constructor(
        private val historicalService: HistoricalService,
    ) {
        private var seasonsCache: List<HistoricalSeasonSummary>? = null
        private val seasonDetailCache = mutableMapOf<Int, HistoricalSeasonDetail>()
        private val seasonGamesCache = mutableMapOf<Int, List<HistoricalGame>>()

        suspend fun getSeasons(force: Boolean = false): List<HistoricalSeasonSummary> {
            if (!force) {
                seasonsCache?.let { return it }
            }

            val seasons =
                historicalService
                    .getHistoricalSeasons()
                    .bodyOrThrow()
                    .seasons
                    .map { it.toDomain() }

            seasonsCache = seasons
            return seasons
        }

        suspend fun getSeasonDetail(
            year: Int,
            force: Boolean = false,
        ): HistoricalSeasonDetail {
            if (!force) {
                seasonDetailCache[year]?.let { return it }
            }

            val seasons = getSeasons(force)
            val seasonSummary = seasons.firstOrNull { it.year == year }
            val urls = seasonSummary?.urls ?: HistoricalSeasonUrls()
            val seasonResponse =
                historicalService
                    .getHistoricalSeasonByUrl(urls.seasonUrl(year))
                    .bodyOrThrow()
            val games = getGames(year, force, urls)
            val seasonTitle = seasonSummary?.title ?: "$SEASON_TITLE_PREFIX $year"
            val overview = parseSeasonOverview(seasonResponse.sections)
            val statsSections = parseStatsSections(loadStatsSections(urls.statsUrl(year)))
            val draftPlayers = parseDraftPlayers(seasonResponse.sections)

            return HistoricalSeasonDetail(
                year = year,
                title = seasonTitle,
                overview = overview,
                statsSections = statsSections,
                draftPlayers = draftPlayers,
                games = games,
            ).also { detail ->
                seasonDetailCache[year] = detail
            }
        }

        suspend fun getGames(
            year: Int,
            force: Boolean = false,
        ): List<HistoricalGame> {
            if (!force) {
                seasonGamesCache[year]?.let { return it }
            }

            val urls = getSeasons(force).firstOrNull { it.year == year }?.urls ?: HistoricalSeasonUrls()
            return getGames(year, force, urls)
        }

        private suspend fun getGames(
            year: Int,
            force: Boolean,
            urls: HistoricalSeasonUrls,
        ): List<HistoricalGame> {
            if (!force) {
                seasonGamesCache[year]?.let { return it }
            }

            val games =
                historicalService
                    .getHistoricalSeasonGamesByUrl(urls.gamesUrl(year))
                    .bodyOrThrow()
                    .games
                    .map { it.toDomain() }

            seasonGamesCache[year] = games
            return games
        }

        suspend fun getGame(
            year: Int,
            gameId: String,
            force: Boolean = false,
        ): HistoricalGame? = getGames(year, force).firstOrNull { it.gameId == gameId }

        private fun HistoricalSeasonSummaryResponse.toDomain() =
            HistoricalSeasonSummary(
                year = year,
                title = title,
                urls =
                    HistoricalSeasonUrls(
                        season = urls.season,
                        stats = urls.stats,
                        games = urls.games,
                    ),
            )

        private fun HistoricalGameResponse.toDomain(): HistoricalGame {
            val scoringSection = sections.findSection(SECTION_SCORING)?.asJsonObjectOrNull()
            val gameInfoSection = sections.findSection(SECTION_GAME_INFO)?.asJsonObjectOrNull()

            return HistoricalGame(
                gameId = gameId,
                title = title,
                date = date,
                slug = slug,
                linescore = parseLinescore(scoringSection?.get(KEY_LINESCORE)),
                scoringSummary = parseScoringSummary(scoringSection?.get(KEY_SCORING_SUMMARY)),
                gameInfo = parseGameInfo(gameInfoSection),
            )
        }

        private fun parseDraftPlayers(sections: Map<String, JsonElement>): List<HistoricalDraftPlayer> {
            val draftSection = sections.findSection(SECTION_DRAFT)
            val players = draftSection?.asJsonArrayOrEmpty().orEmpty()

            return players.mapNotNull { playerElement ->
                val jsonObject = playerElement.asJsonObjectOrNull() ?: return@mapNotNull null
                HistoricalDraftPlayer(columns = jsonObject.toLinkedStringMap())
            }
        }

        private suspend fun loadStatsSections(url: String): Map<String, JsonElement> =
            runCatching {
                historicalService
                    .getHistoricalSeasonStatsByUrl(url)
                    .bodyOrThrow()
                    .sections
            }.getOrDefault(emptyMap())

        private fun parseStatsSections(sections: Map<String, JsonElement>): List<HistoricalStatsSection> =
            sections.mapNotNull { (sectionTitle, sectionElement) ->
                val tables = parseStatsTables(sectionElement)
                if (tables.isEmpty()) {
                    null
                } else {
                    HistoricalStatsSection(
                        title = sectionTitle.toDisplayTitle(),
                        tables = tables,
                    )
                }
            }

        private fun parseStatsTables(sectionElement: JsonElement): List<HistoricalStatsTable> {
            val sectionObject = sectionElement.asJsonObjectOrNull()
            if (sectionObject != null) {
                return sectionObject.entrySet().mapNotNull { (tableTitle, tableElement) ->
                    parseStatsTable(tableTitle, tableElement)
                }
            }

            return listOfNotNull(parseStatsTable("", sectionElement))
        }

        private fun parseStatsTable(
            title: String,
            tableElement: JsonElement,
        ): HistoricalStatsTable? {
            val rows =
                tableElement
                    .asJsonArrayOrEmpty()
                    .orEmpty()
                    .mapNotNull { rowElement ->
                        rowElement.asJsonObjectOrNull()?.toLinkedStringMap()
                    }
            if (rows.isEmpty()) return null

            return HistoricalStatsTable(
                title = title.toDisplayTitle(),
                columns = buildStatsColumns(rows),
                rows = rows,
            )
        }

        private fun buildStatsColumns(rows: List<LinkedHashMap<String, String>>): List<String> {
            val columns = linkedSetOf<String>()
            STATS_PRIORITY_COLUMNS.forEach { priorityColumn ->
                if (rows.any { row -> row.containsKey(priorityColumn) }) {
                    columns += priorityColumn
                }
            }
            rows.forEach { row ->
                row.keys.forEach { column ->
                    columns += column
                }
            }
            return columns.toList()
        }

        private fun parseSeasonOverview(sections: Map<String, JsonElement>): LinkedHashMap<String, String> {
            val source =
                sections
                    .findSection(SECTION_SUMMARY)
                    ?.asJsonObjectOrNull()
                    ?.entrySet()
                    ?.associate { (key, value) -> key.normalizeSectionKey() to value.asStringOrEmpty() }
                    .orEmpty()

            fun pick(aliases: List<String>): String {
                aliases.forEach { alias ->
                    val value = source[alias.normalizeSectionKey()].orEmpty()
                    if (value.isNotBlank()) return value
                }
                return OVERVIEW_VALUE_FALLBACK
            }

            return linkedMapOf(
                OVERVIEW_DIVISION_LABEL to pick(OVERVIEW_DIVISION_ALIASES),
                OVERVIEW_RECORD_LABEL to pick(OVERVIEW_RECORD_ALIASES),
                OVERVIEW_POSITION_LABEL to pick(OVERVIEW_POSITION_ALIASES),
                OVERVIEW_POINTS_FOR_LABEL to pick(OVERVIEW_POINTS_FOR_ALIASES),
                OVERVIEW_POINTS_AGAINST_LABEL to pick(OVERVIEW_POINTS_AGAINST_ALIASES),
                OVERVIEW_HEAD_COACH_LABEL to pick(OVERVIEW_HEAD_COACH_ALIASES),
                OVERVIEW_GENERAL_MANAGER_LABEL to pick(OVERVIEW_GENERAL_MANAGER_ALIASES),
                OVERVIEW_STADIUM_LABEL to pick(OVERVIEW_STADIUM_ALIASES),
                OVERVIEW_TRAINING_CAMP_LABEL to pick(OVERVIEW_TRAINING_CAMP_ALIASES),
                OVERVIEW_PRESIDENT_LABEL to pick(OVERVIEW_PRESIDENT_ALIASES),
            )
        }

        private fun parseLinescore(linescoreElement: JsonElement?): List<HistoricalLineScoreRow> =
            linescoreElement
                .asJsonArrayOrEmpty()
                .orEmpty()
                .mapNotNull { rowElement ->
                    val rowObject = rowElement.asJsonObjectOrNull() ?: return@mapNotNull null
                    val periods =
                        rowObject
                            .entrySet()
                            .filterNot { entry -> entry.key == TEAM_KEY || entry.key == FINAL_KEY }
                            .sortedWith(compareBy({ scoreColumnOrder(it.key) }, { it.key }))
                            .associateTo(LinkedHashMap()) { entry ->
                                entry.key to entry.value.asStringOrEmpty()
                            }

                    HistoricalLineScoreRow(
                        team = rowObject.get(TEAM_KEY).asStringOrEmpty(),
                        periods = periods,
                        finalScore = rowObject.get(FINAL_KEY).asStringOrEmpty(),
                    )
                }

        private fun parseScoringSummary(scoringElement: JsonElement?): List<HistoricalScoringPlay> =
            scoringElement
                .asJsonArrayOrEmpty()
                .orEmpty()
                .mapNotNull { playElement ->
                    val playObject = playElement.asJsonObjectOrNull() ?: return@mapNotNull null
                    HistoricalScoringPlay(
                        quarter = playObject.get(KEY_QUARTER).asStringOrEmpty(),
                        team = playObject.get(KEY_TEAM_NAME).asStringOrEmpty(),
                        description = playObject.get(KEY_DESCRIPTION).asStringOrEmpty(),
                        awayScore = playObject.get(KEY_AWAY_SCORE).asStringOrEmpty(),
                        homeScore = playObject.get(KEY_HOME_SCORE).asStringOrEmpty(),
                        time = playObject.get(KEY_TIME).asStringOrEmpty(),
                    )
                }

        private fun parseGameInfo(gameInfoObject: JsonObject?): LinkedHashMap<String, String> =
            gameInfoObject
                ?.entrySet()
                ?.associateTo(LinkedHashMap()) { entry ->
                    entry.key to entry.value.asStringOrEmpty()
                } ?: LinkedHashMap()

        private fun Map<String, JsonElement>.findSection(expectedName: String): JsonElement? {
            val normalizedName = expectedName.normalizeSectionKey()
            return entries.firstOrNull { (key, _) -> key.normalizeSectionKey() == normalizedName }?.value
        }

        private fun String.normalizeSectionKey(): String =
            Normalizer
                .normalize(lowercase(), Normalizer.Form.NFD)
                .replace(SECTION_MARKS_REGEX, "")

        private fun String.toDisplayTitle(): String =
            split(" ")
                .joinToString(" ") { word ->
                    word.replaceFirstChar { character ->
                        if (character.isLowerCase()) character.titlecase() else character.toString()
                    }
                }

        private fun JsonObject.toLinkedStringMap(): LinkedHashMap<String, String> =
            entrySet().associateTo(LinkedHashMap()) { entry ->
                entry.key to entry.value.asStringOrEmpty()
            }

        private fun JsonElement?.asJsonArrayOrEmpty(): List<JsonElement>? =
            if (this == null || !isJsonArray) {
                null
            } else {
                asJsonArray.toList()
            }

        private fun JsonElement?.asJsonObjectOrNull(): JsonObject? =
            if (this == null || !isJsonObject) {
                null
            } else {
                asJsonObject
            }

        private fun JsonElement?.asStringOrEmpty(): String =
            when {
                this == null || isJsonNull -> ""
                isJsonPrimitive -> asJsonPrimitive.asString.orEmpty()
                else -> toString()
            }

        private fun scoreColumnOrder(label: String): Int =
            when {
                label.toIntOrNull() != null -> label.toIntOrNull() ?: Int.MAX_VALUE
                label.equals(KEY_OVERTIME, ignoreCase = true) -> OVERTIME_SORT_ORDER
                else -> Int.MAX_VALUE - 1
            }

        private fun <T> Response<T>.bodyOrThrow(): T {
            if (!isSuccessful) {
                throw HttpException(this)
            }
            return body() ?: error(HISTORICAL_EMPTY_RESPONSE_ERROR)
        }

        private fun JsonArray.toList(): List<JsonElement> = map { it }

        private fun HistoricalSeasonUrls.seasonUrl(year: Int): String = season.ifBlank { "historical/seasons/$year.json" }

        private fun HistoricalSeasonUrls.statsUrl(year: Int): String = stats.ifBlank { "historical/seasons/$year/stats.json" }

        private fun HistoricalSeasonUrls.gamesUrl(year: Int): String = games.ifBlank { "historical/seasons/$year/games.json" }

        private companion object {
            const val SEASON_TITLE_PREFIX = "Temporada"
            const val SECTION_SCORING = "anotaciones"
            const val SECTION_GAME_INFO = "información del partido"
            const val SECTION_DRAFT = "jugadores seleccionados en el draft"
            const val SECTION_SUMMARY = "resumen"
            const val KEY_LINESCORE = "linescore"
            const val KEY_SCORING_SUMMARY = "scoring_summary"
            const val TEAM_KEY = "Equipo"
            const val FINAL_KEY = "Final"
            const val KEY_QUARTER = "Cuarto"
            const val KEY_TEAM_NAME = "Equipo"
            const val KEY_DESCRIPTION = "Descripción"
            const val KEY_AWAY_SCORE = "Marcador visitante"
            const val KEY_HOME_SCORE = "Marcador local"
            const val KEY_TIME = "Tiempo"
            const val OVERVIEW_DIVISION_LABEL = "División"
            const val OVERVIEW_RECORD_LABEL = "Récord"
            const val OVERVIEW_POSITION_LABEL = "Posición"
            const val OVERVIEW_POINTS_FOR_LABEL = "Puntos a favor"
            const val OVERVIEW_POINTS_AGAINST_LABEL = "Puntos en contra"
            const val OVERVIEW_HEAD_COACH_LABEL = "Head Coach"
            const val OVERVIEW_GENERAL_MANAGER_LABEL = "General Manager"
            const val OVERVIEW_STADIUM_LABEL = "Estadio"
            const val OVERVIEW_TRAINING_CAMP_LABEL = "Training Camp"
            const val OVERVIEW_PRESIDENT_LABEL = "Presidente"
            const val OVERVIEW_VALUE_FALLBACK = "—"
            val OVERVIEW_DIVISION_ALIASES = listOf("división", "division")
            val OVERVIEW_RECORD_ALIASES = listOf("récord", "record", "rcord", "w-l esperado")
            val OVERVIEW_POSITION_ALIASES = listOf("posición", "posicion")
            val OVERVIEW_POINTS_FOR_ALIASES = listOf("puntos a favor")
            val OVERVIEW_POINTS_AGAINST_ALIASES = listOf("puntos en contra")
            val OVERVIEW_HEAD_COACH_ALIASES = listOf("head coach")
            val OVERVIEW_GENERAL_MANAGER_ALIASES = listOf("general manager", "chairman/managing general partner")
            val OVERVIEW_STADIUM_ALIASES = listOf("estadio")
            val OVERVIEW_TRAINING_CAMP_ALIASES = listOf("training camp")
            val OVERVIEW_PRESIDENT_ALIASES = listOf("presidente", "chairman/managing general partner")
            const val HISTORICAL_EMPTY_RESPONSE_ERROR = "La respuesta del histórico llegó vacía"
            const val KEY_OVERTIME = "OT"
            const val OVERTIME_SORT_ORDER = 99
            val STATS_PRIORITY_COLUMNS = listOf("Nombre", "Posición", "Edad", "PJ", "Titularidades", "Premios")
            val SECTION_MARKS_REGEX = "\\p{Mn}+".toRegex()
        }
    }
