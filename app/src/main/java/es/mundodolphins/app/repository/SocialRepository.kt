package es.mundodolphins.app.repository

import es.mundodolphins.app.client.SocialService
import es.mundodolphins.app.viewmodel.SocialUiModel
import es.mundodolphins.app.viewmodel.toSocialUiModel
import javax.inject.Inject

class SocialRepository
    @Inject
    constructor(
        private val socialService: SocialService,
    ) {
    suspend fun getSocialPosts(): List<SocialUiModel> =
        socialService
            .getSocialPosts()
            .mapNotNull { it.toSocialUiModel() }
            .sortedByDescending { it.publishedTimestamp }
}
