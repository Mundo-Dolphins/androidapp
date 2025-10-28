package es.mundodolphins.app.data.articles

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey val id: Long,
    val title: String,
    val author: String,
    val publishedDate: Instant,
    val content: String,
)
