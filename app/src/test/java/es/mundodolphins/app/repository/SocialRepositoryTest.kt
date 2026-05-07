package es.mundodolphins.app.repository

import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.client.SocialService
import es.mundodolphins.app.models.BlueSkyImageResponse
import es.mundodolphins.app.models.BlueSkyPostResponse
import es.mundodolphins.app.models.InstagramPostResponse
import es.mundodolphins.app.models.SocialPostResponse
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SocialRepositoryTest {
    @Test
    fun `getSocialPosts maps ui model and sorts by published desc`() =
        runTest {
            val service =
                object : SocialService {
                    override suspend fun getSocialPosts(): List<SocialPostResponse?> =
                        listOf(
                            socialPost(
                                id = "old",
                                publishedOn = "2026-02-01T10:00:00Z",
                                description = "Older",
                            ),
                            socialPost(
                                id = "new",
                                publishedOn = "2026-02-11T10:00:00Z",
                                description = "Newer",
                            ),
                        )
                }

            val repository = SocialRepository(service)

            val result = repository.getSocialPosts()

            assertThat(result).hasSize(2)
            assertThat(result[0].id).isEqualTo("new")
            assertThat(result[0].description).isEqualTo("Newer")
            assertThat(result[0].postUrl).isEqualTo("https://bsky.app/profile/mundodolphins.es/post/new")
            assertThat(result[0].imageUrls).containsExactly("https://cdn.bsky.app/img/new.jpg")
        }

    @Test
    fun `getSocialPosts filters out nulls from service`() =
        runTest {
            val service =
                object : SocialService {
                    override suspend fun getSocialPosts(): List<SocialPostResponse?> =
                        listOf(
                            socialPost(
                                id = "valid",
                                publishedOn = "2026-02-11T10:00:00Z",
                                description = "Valid",
                            ),
                            null,
                        )
                }

            val repository = SocialRepository(service)

            val result = repository.getSocialPosts()

            assertThat(result).hasSize(1)
            assertThat(result[0].id).isEqualTo("valid")
        }

    @Test
    fun `getSocialPosts maps instagram post when bluesky post is empty`() =
        runTest {
            val service =
                object : SocialService {
                    override suspend fun getSocialPosts(): List<SocialPostResponse?> =
                        listOf(
                            SocialPostResponse(
                                id = "https://www.instagram.com/p/test/",
                                stype = 1,
                                publishedOn = "2026-02-11T10:00:00Z",
                                blueSkyPost =
                                    BlueSkyPostResponse(
                                        bskyUri = "",
                                        bskyCid = "",
                                        description = "",
                                        bskyProfileUri = "",
                                        bskyProfile = "",
                                        bskyPost = "",
                                    ),
                                instagramPost =
                                    InstagramPostResponse(
                                        url = "https://www.instagram.com/p/test/",
                                        description = "Instagram update",
                                        svgPath = "",
                                    ),
                            ),
                        )
                }

            val repository = SocialRepository(service)

            val result = repository.getSocialPosts()

            assertThat(result).hasSize(1)
            assertThat(result[0].description).isEqualTo("Instagram update")
            assertThat(result[0].postUrl).isEqualTo("https://www.instagram.com/p/test/")
            assertThat(result[0].profileName).isEqualTo("Instagram")
        }

    private fun socialPost(
        id: String,
        publishedOn: String,
        description: String,
    ) = SocialPostResponse(
        id = id,
        stype = 0,
        publishedOn = publishedOn,
        blueSkyPost =
            BlueSkyPostResponse(
                bskyUri = "at://did:plc:test/app.bsky.feed.post/$id",
                bskyCid = "bafyreitest$id",
                description = description,
                bskyProfileUri = "https://bsky.app/profile/mundodolphins.es",
                bskyProfile = "@mundodolphins.es - Mundo Dolphins",
                bskyPost = "https://bsky.app/profile/mundodolphins.es/post/$id",
                images =
                    listOf(
                        BlueSkyImageResponse(
                            url = "https://cdn.bsky.app/img/$id.jpg",
                        ),
                    ),
            ),
        instagramPost =
            InstagramPostResponse(
                url = "",
                description = "",
                svgPath = "",
            ),
    )
}
