package es.mundodolphins.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.mundodolphins.app.data.episodes.Episode
import es.mundodolphins.app.data.episodes.EpisodeDao

@Database(entities = [Episode::class], version = 1, exportSchema = false)
@TypeConverters(InstantConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun episodeDao(): EpisodeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val instance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "mundodolphins_database",
                        ).addTypeConverter(InstantConverter())
                        .build()
                INSTANCE = instance
                instance
            }
    }
}
