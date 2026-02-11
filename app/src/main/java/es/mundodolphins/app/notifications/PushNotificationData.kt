package es.mundodolphins.app.notifications

import android.content.Intent

object PushNotificationData {
    const val TYPE_EPISODE = "episode"
    const val TYPE_ARTICLE = "article"

    const val DATA_KEY_TYPE = "type"
    const val DATA_KEY_EPISODE_ID = "episode_id"
    const val DATA_KEY_ARTICLE_PUBLISHED_TIMESTAMP = "article_published_timestamp"

    const val EXTRA_TARGET_TYPE = "push_target_type"
    const val EXTRA_EPISODE_ID = "push_episode_id"
    const val EXTRA_ARTICLE_PUBLISHED_TIMESTAMP = "push_article_published_timestamp"

    sealed interface Target {
        data class Episode(
            val episodeId: Long,
        ) : Target

        data class Article(
            val publishedTimestamp: Long,
        ) : Target
    }

    fun parseTarget(data: Map<String, String>): Target? {
        return when (data[DATA_KEY_TYPE]?.trim()?.lowercase()) {
            TYPE_EPISODE -> {
                data[DATA_KEY_EPISODE_ID]
                    ?.toLongOrNull()
                    ?.let { Target.Episode(it) }
            }

            TYPE_ARTICLE -> {
                data[DATA_KEY_ARTICLE_PUBLISHED_TIMESTAMP]
                    ?.toLongOrNull()
                    ?.let { Target.Article(it) }
            }

            else -> null
        }
    }

    fun parseTarget(intent: Intent?): Target? {
        if (intent == null) return null
        return when (intent.getStringExtra(EXTRA_TARGET_TYPE)) {
            TYPE_EPISODE -> {
                val episodeId = intent.getLongExtra(EXTRA_EPISODE_ID, -1L)
                if (episodeId > 0L) Target.Episode(episodeId) else null
            }

            TYPE_ARTICLE -> {
                val publishedTimestamp = intent.getLongExtra(EXTRA_ARTICLE_PUBLISHED_TIMESTAMP, Long.MIN_VALUE)
                if (publishedTimestamp != Long.MIN_VALUE) Target.Article(publishedTimestamp) else null
            }

            else -> null
        }
    }

    fun applyTarget(intent: Intent, target: Target?) {
        if (target == null) return
        when (target) {
            is Target.Episode -> {
                intent.putExtra(EXTRA_TARGET_TYPE, TYPE_EPISODE)
                intent.putExtra(EXTRA_EPISODE_ID, target.episodeId)
            }

            is Target.Article -> {
                intent.putExtra(EXTRA_TARGET_TYPE, TYPE_ARTICLE)
                intent.putExtra(EXTRA_ARTICLE_PUBLISHED_TIMESTAMP, target.publishedTimestamp)
            }
        }
    }
}
