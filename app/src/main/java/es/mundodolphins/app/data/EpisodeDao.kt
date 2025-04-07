package es.mundodolphins.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.ProvidedTypeConverter
import androidx.room.Query
import androidx.room.TypeConverter
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface EpisodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: Episode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEpisodes(episodes: List<Episode>)

    @Query("SELECT id FROM episodes")
    fun getAllEpisodesIds(): List<Long>

    @Query("SELECT * FROM episodes WHERE id = :episodeId")
    fun getEpisodeById(episodeId: Long): Flow<Episode>

    @Query("SELECT * FROM episodes ORDER BY published DESC LIMIT 30")
    fun getFeed(): Flow<List<Episode>>

    @Query("SELECT DISTINCT season FROM episodes ORDER BY season DESC")
    fun getSeasons(): Flow<List<Int>>

    @Query("SELECT * FROM episodes WHERE season = :seasonId ORDER BY published DESC")
    fun getSeason(seasonId: Int): Flow<List<Episode>>

    @ProvidedTypeConverter
    class Converters {
        @TypeConverter
        fun fromInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

        @TypeConverter
        fun toInstant(instant: Instant?): Long? = instant?.toEpochMilli()
    }
}