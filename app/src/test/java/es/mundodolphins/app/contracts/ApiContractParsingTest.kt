package es.mundodolphins.app.contracts

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import es.mundodolphins.app.models.ArticlesResponse
import es.mundodolphins.app.models.EpisodeResponse
import es.mundodolphins.app.models.SocialPostResponse
import es.mundodolphins.app.models.SocialPostResponseAdapterFactory
import es.mundodolphins.app.models.VideosResponse
import es.mundodolphins.app.models.historical.HistoricalGamesResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonsResponse
import org.junit.Test
import java.io.File

class ApiContractParsingTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapterFactory(SocialPostResponseAdapterFactory())
        .create()

    private fun readExample(fileName: String): String {
        // Assuming the test runs from the root or app directory
        val rootDir = File("..").canonicalFile
        val contractFile = File(rootDir, "contracts/examples/$fileName")
        return contractFile.readText()
    }

    @Test
    fun `parse articles valid example`() {
        val json = readExample("articles.valid.json")
        val type = object : TypeToken<List<ArticlesResponse>>() {}.type
        val result: List<ArticlesResponse> = gson.fromJson(json, type)
        assertThat(result).isNotEmpty()
        assertThat(result[0].title).isEqualTo("Miami Dolphins Schedule 2026")
    }

    @Test
    fun `parse episodes valid example`() {
        val json = readExample("episodes.valid.json")
        val type = object : TypeToken<List<EpisodeResponse>>() {}.type
        val result: List<EpisodeResponse> = gson.fromJson(json, type)
        assertThat(result).isNotEmpty()
        assertThat(result[0].title).isEqualTo("Episode 1: The Victory")
    }

    @Test
    fun `parse seasons valid example`() {
        val json = readExample("seasons.valid.json")
        val type = object : TypeToken<List<String>>() {}.type
        val result: List<String> = gson.fromJson(json, type)
        assertThat(result).hasSize(3)
        assertThat(result).contains("2026")
    }

    @Test
    fun `parse videos valid example`() {
        val json = readExample("videos.valid.json")
        val result: VideosResponse = gson.fromJson(json, VideosResponse::class.java)
        assertThat(result.videos).isNotEmpty()
        assertThat(result.videos[0].title).isEqualTo("Highlight: Dolphins vs Patriots")
    }

    @Test
    fun `parse social valid example`() {
        val json = readExample("social.valid.json")
        val type = object : TypeToken<List<SocialPostResponse?>>() {}.type
        val result: List<SocialPostResponse?> = gson.fromJson(json, type)
        assertThat(result).hasSize(3)
        assertThat(result[0]?.id).contains("3melauwf6ks2u")
        assertThat(result[1]).isNull() // The string element should be null due to our safe adapter
        assertThat(result[2]?.id).isEqualTo("ig_123")
    }

    @Test
    fun `parse historical seasons valid example`() {
        val json = readExample("historical-seasons.valid.json")
        val result: HistoricalSeasonsResponse = gson.fromJson(json, HistoricalSeasonsResponse::class.java)
        assertThat(result.seasons).isNotEmpty()
        assertThat(result.seasons[0].year).isEqualTo(2024)
    }

    @Test
    fun `parse historical games valid example`() {
        val json = readExample("historical-games.valid.json")
        val result: HistoricalGamesResponse = gson.fromJson(json, HistoricalGamesResponse::class.java)
        assertThat(result.games).isNotEmpty()
        assertThat(result.games[0].gameId).isEqualTo("2024_01")
    }

    @Test
    fun `gson ignores unknown fields by default`() {
        val json = """
            [
              {
                "title": "Test Article",
                "unknown_field": "some value"
              }
            ]
        """.trimIndent()
        val type = object : TypeToken<List<ArticlesResponse>>() {}.type
        val result: List<ArticlesResponse> = gson.fromJson(json, type)
        assertThat(result).hasSize(1)
        assertThat(result[0].title).isEqualTo("Test Article")
    }
}
