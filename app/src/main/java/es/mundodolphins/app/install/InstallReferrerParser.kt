package es.mundodolphins.app.install

/**
 * Parses a raw Google Play Install Referrer string to extract app-specific parameters.
 *
 * The Play Store URL-decodes the referrer value before delivering it, so the app receives
 * a plain query-string like `episodeId=1704067200000&utm_source=web` rather than its
 * percent-encoded form.
 */
object InstallReferrerParser {
    private const val EPISODE_ID_KEY = "episodeId"

    /**
     * Returns the `episodeId` parsed from [referrer], or `null` if the referrer is blank,
     * does not contain the key, or the value cannot be converted to a [Long].
     */
    fun parseEpisodeId(referrer: String?): Long? {
        if (referrer.isNullOrBlank()) return null
        return referrer
            .split("&")
            .firstNotNullOfOrNull { part ->
                val eqIdx = part.indexOf('=')
                if (eqIdx > 0 &&
                    part.substring(0, eqIdx) == EPISODE_ID_KEY
                ) {
                    part.substring(eqIdx + 1).toLongOrNull()
                } else {
                    null
                }
            }
    }
}
