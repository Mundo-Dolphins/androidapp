package es.mundodolphins.app.data.articles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllArticles(articles: List<Article>)

    @Query("SELECT id FROM articles")
    fun getAllArticlesIds(): List<Long>

    @Query("SELECT * FROM articles WHERE id = :articleId")
    fun getArticleById(articleId: Long): Flow<Article>

    @Query("SELECT * FROM articles ORDER BY publishedDate DESC LIMIT 30")
    fun getFeed(): Flow<List<Article>>
}
