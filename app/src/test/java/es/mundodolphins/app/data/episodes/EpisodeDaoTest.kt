package es.mundodolphins.app.data.episodes

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import es.mundodolphins.app.data.InstantConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.util.concurrent.Executors
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class EpisodeDaoTest {

    // No need to override Dispatchers.Main for DAO tests

    @Database(entities = [Episode::class], version = 1, exportSchema = false)
    @TypeConverters(InstantConverter::class)
    abstract class TestDatabase : RoomDatabase() {
        abstract fun episodeDao(): EpisodeDao
    }

    private lateinit var episodeDao: EpisodeDao
    private lateinit var db: TestDatabase

    @Before
    fun createDb() {
        val context = RuntimeEnvironment.getApplication().applicationContext
        val queryExecutor = Executors.newSingleThreadExecutor()
        val transactionExecutor = Executors.newSingleThreadExecutor()

        db = Room.inMemoryDatabaseBuilder(context, TestDatabase::class.java)
            .addTypeConverter(InstantConverter())
            .setQueryExecutor(queryExecutor)
            .setTransactionExecutor(transactionExecutor)
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
        val insertedEpisode = withContext(Dispatchers.IO) { episodeDao.getEpisodeById(1).first() }
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
        val allEpisodes = withContext(Dispatchers.IO) { episodeDao.getSeason(1).first() }
        Truth.assertThat(allEpisodes).hasSize(2)
        Truth.assertThat(allEpisodes).containsExactlyElementsIn(episodes)
    }

    @Test
    fun `should get an episodes from DB`() = runTest {
        // Given
        val episode = getTestEpisodes(1).first()
        episodeDao.insertEpisode(episode)

        // Then
        val fetched = withContext(Dispatchers.IO) { episodeDao.getEpisodeById(1).first() }
        Truth.assertThat(fetched).isEqualTo(episode)
    }

    @Test
    fun `should get all episode IDs from DB`() = runTest {
        // Given
        val episodes = getTestEpisodes(2)
        episodeDao.insertAllEpisodes(episodes)

        // Then
        val ids = withContext(Dispatchers.IO) { episodeDao.getAllEpisodesIds() }
        Truth.assertThat(ids).containsExactly(1L, 2L)
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
                listeningStatus = Episode.ListeningStatus.NOT_LISTENED
            )
        }
        episodeDao.insertAllEpisodes(episodes)

        // When
        val feed = withContext(Dispatchers.IO) { episodeDao.getFeed().first() }

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
                listeningStatus = Episode.ListeningStatus.NOT_LISTENED
            )
        }
        episodeDao.insertAllEpisodes(episodes)

        // Then
        val seasons = withContext(Dispatchers.IO) { episodeDao.getSeasons().first() }
        Truth.assertThat(seasons)
            .containsExactly(10, 9, 8, 7, 6, 5, 4, 3, 2, 1)
            .inOrder()
    }

    @Test
    fun `should get all episodes from a given season`() = runTest {
        // Given
        val episodes = getTestEpisodes(2)
        episodeDao.insertAllEpisodes(episodes)

        // When
        val allEpisodes = withContext(Dispatchers.IO) { episodeDao.getSeason(1).first() }

        // Then
        Truth.assertThat(allEpisodes).hasSize(2)
        Truth.assertThat(allEpisodes).containsExactlyElementsIn(episodes)
    }

    @Test
    fun `should not get any apisode for given season as it's not on DB`() = runTest {
        val season99 = withContext(Dispatchers.IO) { episodeDao.getSeason(99).first() }
        Truth.assertThat(season99).isEmpty()
    }

    // --- Extra tests added ---

    @Test
    fun `insert with same id replaces existing`() = runTest {
        val original = Episode(
            id = 100L,
            title = "Original",
            description = "orig",
            audio = "http://audio/orig.mp3",
            listenedProgress = 0,
            published = Instant.parse("2025-01-01T00:00:00Z"),
            imgMain = "",
            imgMini = "",
            len = "00:30:00",
            link = "",
            season = 1,
            listeningStatus = Episode.ListeningStatus.NOT_LISTENED
        )
        val updated = original.copy(
            title = "Updated",
            listenedProgress = 12345,
            listeningStatus = Episode.ListeningStatus.LISTENED
        )

        // Insert original and assert
        episodeDao.insertEpisode(original)
        Truth.assertThat(withContext(Dispatchers.IO) { episodeDao.getEpisodeById(original.id).first() }).isEqualTo(original)

        // Insert updated with same id -> should replace
        episodeDao.insertEpisode(updated)
        Truth.assertThat(withContext(Dispatchers.IO) { episodeDao.getEpisodeById(original.id).first() }).isEqualTo(updated)
    }

    @Test
    fun getAllEpisodesIdsReturnsEmptyWhenNoData() = runTest {
        Truth.assertThat(withContext(Dispatchers.IO) { episodeDao.getAllEpisodesIds() }).isEmpty()
    }

    @Test
    fun replaceChangesPublishedDateAffectsFeedOrder() = runTest {
        val e1 = Episode(
            id = 201L,
            title = "Early",
            description = "",
            audio = "",
            listenedProgress = 0,
            published = Instant.parse("2025-01-01T00:00:00Z"),
            imgMain = "",
            imgMini = "",
            len = "00:10:00",
            link = "",
            season = 1,
            listeningStatus = Episode.ListeningStatus.NOT_LISTENED
        )
        val e2 = Episode(
            id = 202L,
            title = "Middle",
            description = "",
            audio = "",
            listenedProgress = 0,
            published = Instant.parse("2025-02-01T00:00:00Z"),
            imgMain = "",
            imgMini = "",
            len = "00:10:00",
            link = "",
            season = 1,
            listeningStatus = Episode.ListeningStatus.NOT_LISTENED
        )

        episodeDao.insertAllEpisodes(listOf(e1, e2))
        var feed = withContext(Dispatchers.IO) { episodeDao.getFeed().first() }
        Truth.assertThat(feed.first().id).isEqualTo(202L)

        // Replace e1 with a newer published date so it should come first
        val e1Updated = e1.copy(published = Instant.parse("2025-03-01T00:00:00Z"))
        episodeDao.insertEpisode(e1Updated)

        feed = withContext(Dispatchers.IO) { episodeDao.getFeed().first() }
        Truth.assertThat(feed.first().id).isEqualTo(201L)
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
            listeningStatus = Episode.ListeningStatus.NOT_LISTENED,
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
            listeningStatus = Episode.ListeningStatus.LISTENING,
        )
    ).take(takeEpisodes)
}