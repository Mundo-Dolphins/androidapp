package es.mundodolphins.app.data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.Instant

@ProvidedTypeConverter
class InstantConverter {
    @TypeConverter
    fun fromInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun toInstant(instant: Instant?): Long? = instant?.toEpochMilli()
}
