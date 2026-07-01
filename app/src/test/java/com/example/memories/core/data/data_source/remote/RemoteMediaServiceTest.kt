package com.example.memories.core.data.data_source.remote

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RemoteMediaServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var service: RemoteMediaService

    private val apiKey = "test-key-123"

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .build()

        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteMediaService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    // ---------------- (1) request shape ----------------

    @Test
    fun imageRequest_usesCuratedPathWithPageAndPerPageQuery() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody(IMAGE_JSON))

        service.getRemoteImageMediaResponse(page = 2, perPage = 15)

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/curated/?page=2&per_page=15", request.path)
        assertEquals(apiKey, request.getHeader("Authorization"))
    }

    @Test
    fun videoRequest_usesPopularPathWithDefaultDurationQueryAndAuthHeader() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody(VIDEO_JSON))

        // perPage/minDuration/maxDuration left as defaults (30 / 15 / 30)
        service.getRemoteVideoMediaResponse(page = 3)

        val request = server.takeRequest()
        assertEquals(
            "/videos/popular?page=3&per_page=30&min_duration=15&max_duration=30",
            request.path
        )
        assertEquals(apiKey, request.getHeader("Authorization"))
    }

    // ---------------- (2) response deserialization ----------------

    @Test
    fun imageResponse_deserializesPhotosAndSerializedNameFields() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody(IMAGE_JSON))

        val response = service.getRemoteImageMediaResponse(page = 2, perPage = 15)

        assertEquals(2, response.page)
        assertNull(response.prevPage)
        assertEquals("https://api.pexels.com/v1/curated/?page=3", response.nextPage)
        assertEquals(1, response.photos.size)
        val photo = response.photos.first()
        assertEquals(101L, photo.id)
        assertEquals("https://jane.example", photo.photographerUrl)  // @SerializedName photographer_url
        assertEquals(5L, photo.photographerId)                        // @SerializedName photographer_id
        assertEquals("#abcdef", photo.avgColor)                       // @SerializedName avg_color
        assertEquals("portrait.jpg", photo.src.portrait)
    }

    @Test
    fun imageResponse_nullNextPageDeserializesToNull() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody(IMAGE_JSON_LAST_PAGE))

        val response = service.getRemoteImageMediaResponse(page = 9, perPage = 15)

        // the paging source relies on this being null to stop appending
        assertNull(response.nextPage)
    }

    @Test
    fun videoResponse_deserializesNestedFilesAndComputesDownloadLink() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody(VIDEO_JSON))

        val response = service.getRemoteVideoMediaResponse(page = 3)

        assertEquals(1, response.videos.size)
        val video = response.videos.first()
        assertEquals(7L, video.id)
        assertEquals("https://bob.example", video.user.profileUrl)   // @SerializedName url
        assertEquals(2, video.videoFiles.size)
        // file_type (@SerializedName) parsed correctly + downloadLink prefers hd mp4
        assertEquals("https://cdn.example/hd.mp4", video.downloadLink)
    }

    companion object {
        private val IMAGE_JSON = """
            {
              "page": 2,
              "per_page": 15,
              "prev_page": null,
              "next_page": "https://api.pexels.com/v1/curated/?page=3",
              "photos": [
                {
                  "id": 101,
                  "width": 1200,
                  "height": 800,
                  "url": "https://pexels.example/photo/101",
                  "photographer": "Jane",
                  "photographer_url": "https://jane.example",
                  "photographer_id": 5,
                  "avg_color": "#abcdef",
                  "alt": "a nice photo",
                  "src": { "portrait": "portrait.jpg" }
                }
              ]
            }
        """.trimIndent()

        private val IMAGE_JSON_LAST_PAGE = """
            {
              "page": 9,
              "per_page": 15,
              "prev_page": "https://api.pexels.com/v1/curated/?page=8",
              "next_page": null,
              "photos": []
            }
        """.trimIndent()

        private val VIDEO_JSON = """
            {
              "page": 3,
              "per_page": 30,
              "prev_page": null,
              "next_page": null,
              "videos": [
                {
                  "id": 7,
                  "width": 1920,
                  "height": 1080,
                  "url": "https://pexels.example/video/7",
                  "image": "https://cdn.example/thumb.jpg",
                  "duration": 25,
                  "user": { "id": 3, "name": "Bob", "url": "https://bob.example" },
                  "video_files": [
                    { "id": 1, "quality": "sd", "file_type": "video/mp4", "width": 640, "height": 360, "link": "https://cdn.example/sd.mp4" },
                    { "id": 2, "quality": "hd", "file_type": "video/mp4", "width": 1920, "height": 1080, "link": "https://cdn.example/hd.mp4" }
                  ]
                }
              ]
            }
        """.trimIndent()
    }
}
