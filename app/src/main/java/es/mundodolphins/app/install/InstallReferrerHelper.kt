package es.mundodolphins.app.install

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Queries the Google Play Install Referrer API and persists a one-time "processed" flag so the
 * referrer is consumed only once per install.
 *
 * Call [isAlreadyProcessed] before [queryEpisodeId] to skip repeat queries on subsequent launches.
 * Call [markProcessed] after handling the result to prevent future re-queries.
 */
object InstallReferrerHelper {
    private const val PREFS_NAME = "install_referrer"
    private const val KEY_PROCESSED = "processed"

    fun isAlreadyProcessed(context: Context): Boolean =
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_PROCESSED, false)

    fun markProcessed(context: Context) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_PROCESSED, true)
            .apply()
    }

    /**
     * Connects to the Play Install Referrer service and returns the episode ID encoded in the
     * referrer string, or `null` if the referrer is absent, unreadable, or contains no episode ID.
     *
     * This is a suspending function; call it from a [kotlinx.coroutines.CoroutineScope] and it
     * will not block the calling thread.
     */
    suspend fun queryEpisodeId(context: Context): Long? =
        try {
            val referrer = queryRawReferrer(context)
            InstallReferrerParser.parseEpisodeId(referrer)
        } catch (e: CancellationException) {
            throw e
        } catch (ignored: Exception) {
            null
        }

    private suspend fun queryRawReferrer(context: Context): String? =
        suspendCancellableCoroutine { continuation ->
            val client =
                InstallReferrerClient
                    .newBuilder(context.applicationContext)
                    .build()

            continuation.invokeOnCancellation {
                runCatching { client.endConnection() }
            }

            client.startConnection(
                object : InstallReferrerStateListener {
                    override fun onInstallReferrerSetupFinished(responseCode: Int) {
                        val referrer =
                            if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                                runCatching { client.installReferrer.installReferrer }.getOrNull()
                            } else {
                                null
                            }
                        runCatching { client.endConnection() }
                        if (continuation.isActive) continuation.resume(referrer)
                    }

                    override fun onInstallReferrerServiceDisconnected() {
                        if (continuation.isActive) continuation.resume(null)
                    }
                },
            )
        }
}
