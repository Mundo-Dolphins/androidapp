package es.mundodolphins.app.notifications

import android.content.Intent
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class PushNotificationDataTest {
    @Test
    fun `parseTarget maps episode payload`() {
        val target =
            PushNotificationData.parseTarget(
                mapOf(
                    PushNotificationData.DATA_KEY_TYPE to PushNotificationData.TYPE_EPISODE,
                    PushNotificationData.DATA_KEY_EPISODE_ID to "12345",
                ),
            )

        assertThat(target).isEqualTo(PushNotificationData.Target.Episode(12345L))
    }

    @Test
    fun `parseTarget maps article payload`() {
        val target =
            PushNotificationData.parseTarget(
                mapOf(
                    PushNotificationData.DATA_KEY_TYPE to PushNotificationData.TYPE_ARTICLE,
                    PushNotificationData.DATA_KEY_ARTICLE_PUBLISHED_TIMESTAMP to "1733262460000",
                ),
            )

        assertThat(target).isEqualTo(PushNotificationData.Target.Article(1733262460000L))
    }

    @Test
    fun `applyTarget writes and parseTarget from intent reads the same values`() {
        val intent = Intent()
        PushNotificationData.applyTarget(intent, PushNotificationData.Target.Episode(55L))

        val parsed = PushNotificationData.parseTarget(intent)

        assertThat(parsed).isEqualTo(PushNotificationData.Target.Episode(55L))
    }

    @Test
    fun `parseTarget returns null for unsupported payload`() {
        val target =
            PushNotificationData.parseTarget(
                mapOf(
                    PushNotificationData.DATA_KEY_TYPE to "unknown",
                ),
            )

        assertThat(target).isNull()
    }
}
