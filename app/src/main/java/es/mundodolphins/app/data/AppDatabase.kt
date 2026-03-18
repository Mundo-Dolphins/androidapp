package es.mundodolphins.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.mundodolphins.app.data.episodes.Episode
import es.mundodolphins.app.data.episodes.EpisodeDao

@Database(entities = [Episode::class], version = 1, exportSchema = false)
@TypeConverters(InstantConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun episodeDao(): EpisodeDao
}
