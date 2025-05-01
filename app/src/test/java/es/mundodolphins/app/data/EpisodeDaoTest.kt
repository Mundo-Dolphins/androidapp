package es.mundodolphins.app.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.common.truth.Truth
import es.mundodolphins.app.data.Episode.ListeningStatus.LISTENING
import es.mundodolphins.app.data.Episode.ListeningStatus.NOT_LISTENED
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class EpisodeDaoTest {
    private lateinit var context: Context

    private lateinit var episodeDao: EpisodeDao

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        context = RuntimeEnvironment.getApplication()

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .addTypeConverter(EpisodeDao.Converters())
            .build()
        episodeDao = db.episodeDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `should insert the episodes onto DB`() = runTest {
        // Given
        val episode = getTestEpisodes().first()

        // When
        episodeDao.insertEpisode(episode)

        // Then
        val insertedEpisode = episodeDao.getEpisodeById(1).first()
        Truth.assertThat(insertedEpisode).isNotNull()
        Truth.assertThat(insertedEpisode).isEqualTo(episode)
    }

    @Test
    fun `should insert all episodes onto DB`() = runTest {
        // Given
        val episodes = getTestEpisodes(2)

        // When
        episodeDao.insertAllEpisodes(episodes)

        // Then
        val allEpisodes = episodeDao.getSeason(1).first()
        Truth.assertThat(allEpisodes).hasSize(2)
        Truth.assertThat(allEpisodes).containsExactlyElementsIn(episodes)
    }

    @Test
    fun `should get an episodes from DB`() = runTest {
        // Given
        val episode = getTestEpisodes(1).first()
        episodeDao.insertEpisode(episode)

        // Then
        Truth.assertThat(episodeDao.getEpisodeById(1).first()).isEqualTo(episode)
    }

    @Test
    fun `should get all episode IDs from DB`() = runTest {
        // Given
        val episodes = getTestEpisodes(2)
        episodeDao.insertAllEpisodes(episodes)

        // Then
        Truth.assertThat(episodeDao.getAllEpisodesIds()).containsExactly(1L, 2L)
    }

    @Test
    fun `should get the latest episodes`() = runTest {
        // Given
        val episodes = (1..35).map {
            Episode(
                id = it.toLong(),
                title = "Ep $it",
                description = "Desc $it",
                audio = "http://audio/$it.mp3",
                listenedProgress = 0,
                published = Instant.parse(
                    "2025-03-${
                        (it % 28 + 1).toString().padStart(2, '0')
                    }T12:00:00Z"
                ),
                imgMain = "",
                imgMini = "",
                len = "01:00:00",
                link = "",
                season = 1,
                listeningStatus = NOT_LISTENED
            )
        }
        episodeDao.insertAllEpisodes(episodes)

        // When
        val feed = episodeDao.getFeed().first()

        // Then
        Truth.assertThat(feed).hasSize(30)
        Truth.assertThat(feed.first().published).isGreaterThan(feed.last().published)
    }

    @Test
    fun `should get all seasons on DB`() = runTest {
        // Given
        val episodes = (1..10).map {
            Episode(
                id = it.toLong(),
                title = "Ep $it",
                description = "Desc $it",
                audio = "http://audio/$it.mp3",
                listenedProgress = 0,
                published = Instant.parse(
                    "2025-03-${
                        (it % 28 + 1).toString().padStart(2, '0')
                    }T12:00:00Z"
                ),
                imgMain = "",
                imgMini = "",
                len = "01:00:00",
                link = "",
                season = it,
                listeningStatus = NOT_LISTENED
            )
        }
        episodeDao.insertAllEpisodes(episodes)

        // Then
        Truth.assertThat(episodeDao.getSeasons().first())
            .containsExactly(10, 9, 8, 7, 6, 5, 4, 3, 2, 1)
            .inOrder()
    }

    @Test
    fun `should get all episodes from a given season`() = runTest {
        // Given
        val episodes = getTestEpisodes(2)
        episodeDao.insertAllEpisodes(episodes)

        // When
        val allEpisodes = episodeDao.getSeason(1).first()

        // Then
        Truth.assertThat(allEpisodes).hasSize(2)
        Truth.assertThat(allEpisodes).containsExactlyElementsIn(episodes)
    }

    @Test
    fun `should not get any apisode for given season as it's not on DB`() = runTest {
        Truth.assertThat(episodeDao.getSeason(99).first()).isEmpty()
    }

    private fun getTestEpisodes(takeEpisodes: Int = 1) = listOf(
        Episode(
            id = 1,
            title = "Test Episode",
            description = "This is a test episode",
            audio = "http://example.com/episode.mp3",
            listenedProgress = 0,
            published = Instant.parse("2025-02-20T15:44:39Z"),
            imgMain = "https://wwww.image.com/1.jpg",
            imgMini = "TODO()",
            len = "01:29:55",
            link = "https://www.episode.com/1",
            season = 1,
            listeningStatus = NOT_LISTENED,
        ),
        Episode(
            id = 2,
            title = "Test Episode",
            description = "This is a test episode",
            audio = "http://example.com/episode.mp3",
            listenedProgress = 0,
            published = Instant.parse("2025-02-20T15:44:39Z"),
            imgMain = "https://wwww.image.com/2.jpg",
            imgMini = "TODO()",
            len = "01:29:55",
            link = "https://www.episode.com/2",
            season = 1,
            listeningStatus = LISTENING,
        )
    ).take(takeEpisodes)
}