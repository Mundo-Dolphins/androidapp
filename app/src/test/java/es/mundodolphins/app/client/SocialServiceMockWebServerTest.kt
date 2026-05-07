package es.mundodolphins.app.client

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import es.mundodolphins.app.models.SocialPostResponseAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class SocialServiceMockWebServerTest {
    @Test
    fun `getSocialPosts via MockWebServer parses response and hits correct path`() =
        runTest {
            System.setProperty("okhttp.platform", "jdk")

            val server = MockWebServer()
            server.start()

            val body =
                """
                [
                  {
                    "id": "https://bsky.app/profile/mundodolphins.es/post/3melauwf6ks2u",
                    "stype": 0,
                    "PublishedOn": "2026-02-11T10:19:00Z",
                    "BlueSkyPost": {
                      "BskyURI": "at://did:plc:nkx2wlw3rno7dqmzbj5cysme/app.bsky.feed.post/3melauwf6ks2u",
                      "BskyCID": "bafyreiaxmwtror6art5nxk3qdl3n3d45ptjpzbjcl5ufgqkxrmvbi5t42y",
                      "Description": "Segundo fichaje de Sully para los Dolphins",
                      "BskyProfileURI": "https://bsky.app/profile/mundodolphins.es",
                      "BskyProfile": "@mundodolphins.es - Mundo Dolphins",
                      "BskyPost": "https://bsky.app/profile/mundodolphins.es/post/3melauwf6ks2u"
                    },
                    "InstagramPost": {
                      "URL": "",
                      "Description": "",
                      "SvgPath": ""
                    }
                  }
                ]
                """.trimIndent()

            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(body)
                    .addHeader("Content-Type", "application/json"),
            )

            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl(server.url("/"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val service = retrofit.create(SocialService::class.java)
            val response = service.getSocialPosts()

            assertThat(response).hasSize(1)
            assertThat(response.first()?.blueSkyPost?.bskyProfile).isEqualTo("@mundodolphins.es - Mundo Dolphins")
            assertThat(response.first()?.publishedTimestamp).isEqualTo(1770805140000L)

            val request = server.takeRequest()
            assertThat(request.path).isEqualTo("/social.json")

            server.shutdown()
        }

    @Test
    fun `getSocialPosts succeeds when response contains a string instead of an object`() =
        runTest {
            System.setProperty("okhttp.platform", "jdk")

            val server = MockWebServer()
            server.start()

            val body =
                """
                [
                  {
                    "id": "1",
                    "stype": 0,
                    "PublishedOn": "2026-02-11T10:19:00Z",
                    "BlueSkyPost": {
                      "BskyURI": "uri1",
                      "BskyCID": "cid1",
                      "Description": "Desc 1",
                      "BskyProfileURI": "p-uri1",
                      "BskyProfile": "profile1",
                      "BskyPost": "post1"
                    },
                    "InstagramPost": null
                  },
                  "This is an unexpected string",
                  {
                    "id": "2",
                    "stype": 0,
                    "PublishedOn": "2026-02-11T10:20:00Z",
                    "BlueSkyPost": {
                      "BskyURI": "uri2",
                      "BskyCID": "cid2",
                      "Description": "Desc 2",
                      "BskyProfileURI": "p-uri2",
                      "BskyProfile": "profile2",
                      "BskyPost": "post2"
                    },
                    "InstagramPost": null
                  }
                ]
                """.trimIndent()

            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(body)
                    .addHeader("Content-Type", "application/json"),
            )

            val gson =
                GsonBuilder()
                    .registerTypeAdapterFactory(SocialPostResponseAdapterFactory())
                    .create()

            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl(server.url("/"))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            val service = retrofit.create(SocialService::class.java)
            val posts = service.getSocialPosts()

            assertThat(posts).hasSize(3)
            assertThat(posts[0]?.id).isEqualTo("1")
            assertThat(posts[1]).isNull()
            assertThat(posts[2]?.id).isEqualTo("2")

            server.shutdown()
        }
}
