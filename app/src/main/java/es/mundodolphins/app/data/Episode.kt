package es.mundodolphins.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import es.mundodolphins.app.data.Episode.ListeningStatus.NOT_LISTENED
import java.time.Instant
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ofPattern

@Entity(tableName = "episodes")
data class Episode(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String,
    val audio: String,
    val published: Instant,
    @ColumnInfo(name = "img_main")
    val imgMain: String,
    @ColumnInfo(name = "img_mini")
    val imgMini: String?,
    val len: String,
    val link: String,
    val season: Int,
    @ColumnInfo(defaultValue = "0")
    var listenedProgress: Long = 0,
    @ColumnInfo(defaultValue = "NOT_LISTENED")
    var listeningStatus: ListeningStatus = NOT_LISTENED,
) {
    val publishedOn: String
        get() = published.atOffset(UTC).format(ofPattern("dd/MM/yyyy HH:mm"))

    enum class ListeningStatus {
        NOT_LISTENED,
        LISTENING,
        LISTENED
    }
}
