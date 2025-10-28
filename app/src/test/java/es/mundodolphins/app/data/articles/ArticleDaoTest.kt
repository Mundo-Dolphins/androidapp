package es.mundodolphins.app.data.articles

import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import com.google.common.truth.Truth
import es.mundodolphins.app.data.InstantConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.IOException
import java.time.Instant
import java.util.concurrent.Executors

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ArticleDaoTest {
    // No need to override Dispatchers.Main for DAO tests

    // Define a small in-memory Room database for testing that only includes Article
    @Database(entities = [Article::class], version = 1, exportSchema = false)
    @TypeConverters(InstantConverter::class)
    abstract class TestDatabase : androidx.room.RoomDatabase() {
        abstract fun articleDao(): ArticleDao
    }

    private lateinit var db: TestDatabase
    private lateinit var dao: ArticleDao

    @Before
    fun createDb() {
        val context = RuntimeEnvironment.getApplication().applicationContext
        val queryExecutor = Executors.newSingleThreadExecutor()
        val transactionExecutor = Executors.newSingleThreadExecutor()

        db =
            Room
                .inMemoryDatabaseBuilder(context, TestDatabase::class.java)
                .addTypeConverter(InstantConverter())
                .setQueryExecutor(queryExecutor)
                .setTransactionExecutor(transactionExecutor)
                .build()
        dao = db.articleDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `should insert and get article by id`() =
        runTest {
            val article = Article(1L, "Title", "Author", Instant.ofEpochMilli(1000), "Content")
            dao.insertArticle(article)

            val fromDb = withContext(Dispatchers.IO) { dao.getArticleById(1L).first() }
            Truth.assertThat(fromDb).isEqualTo(article)
        }

    @Test
    fun `should insert all articles and get all ids`() =
        runTest {
            val articles =
                listOf(
                    Article(1L, "A1", "Author1", Instant.ofEpochMilli(1000), "C1"),
                    Article(2L, "A2", "Author2", Instant.ofEpochMilli(2000), "C2"),
                )
            dao.insertAllArticles(articles)
            val ids = withContext(Dispatchers.IO) { dao.getAllArticlesIds() }
            Truth.assertThat(ids.sorted()).containsExactly(1L, 2L).inOrder()
        }

    @Test
    fun `should get feed ordered by published date desc`() =
        runTest {
            val a1 = Article(1L, "Old", "A", Instant.ofEpochMilli(1000), "C")
            val a2 = Article(2L, "New", "B", Instant.ofEpochMilli(3000), "C")
            val a3 = Article(3L, "Mid", "C", Instant.ofEpochMilli(2000), "C")
            dao.insertAllArticles(listOf(a1, a2, a3))
            val feed = withContext(Dispatchers.IO) { dao.getFeed().first() }
            Truth.assertThat(feed).containsExactly(a2, a3, a1).inOrder()
        }

    // --- Extra tests ---

    @Test
    fun `insert with same id replaces existing`() =
        runTest {
            val original = Article(100L, "Original", "Auth", Instant.parse("2025-01-01T00:00:00Z"), "Orig")
            val updated = original.copy(title = "Updated")

            dao.insertArticle(original)
            val fetchedOriginal = withContext(Dispatchers.IO) { dao.getArticleById(original.id).first() }
            Truth.assertThat(fetchedOriginal).isEqualTo(original)

            dao.insertArticle(updated)
            val fetchedUpdated = withContext(Dispatchers.IO) { dao.getArticleById(original.id).first() }
            Truth.assertThat(fetchedUpdated).isEqualTo(updated)
        }

    @Test
    fun getAllArticlesIdsReturnsEmptyWhenNoData() =
        runTest {
            val idsEmpty = withContext(Dispatchers.IO) { dao.getAllArticlesIds() }
            Truth.assertThat(idsEmpty).isEmpty()
        }

    @Test
    fun replaceChangesPublishedDateAffectsFeedOrder() =
        runTest {
            val a1 = Article(201L, "Early", "A", Instant.parse("2025-01-01T00:00:00Z"), "")
            val a2 = Article(202L, "Later", "B", Instant.parse("2025-02-01T00:00:00Z"), "")

            dao.insertAllArticles(listOf(a1, a2))
            var feed = withContext(Dispatchers.IO) { dao.getFeed().first() }
            Truth.assertThat(feed.first().id).isEqualTo(202L)

            val a1Updated = a1.copy(publishedDate = Instant.parse("2025-03-01T00:00:00Z"))
            dao.insertArticle(a1Updated)

            feed = withContext(Dispatchers.IO) { dao.getFeed().first() }
            Truth.assertThat(feed.first().id).isEqualTo(201L)
        }
}
