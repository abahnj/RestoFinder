package com.wolt.restofinder.data.remote.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wolt.restofinder.data.mapper.toDomain
import com.wolt.restofinder.data.remote.exception.ServerException
import com.wolt.restofinder.data.remote.interceptor.ErrorInterceptor
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class WoltApiIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: WoltApi
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val client = OkHttpClient.Builder()
            .addInterceptor(ErrorInterceptor())
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        api = retrofit.create(WoltApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getRestaurants returns valid response with venues`() = runTest {
        // Given: Mock server returns successful response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(VALID_RESPONSE_JSON)

        mockWebServer.enqueue(mockResponse)

        // When: API call is made
        val response = api.getRestaurants(latitude = 60.17, longitude = 24.94)

        // Then: Response is parsed correctly
        assertNotNull(response)
        assertEquals(2, response.sections.size)

        // Verify first section (categories)
        val firstSection = response.sections[0]
        assertEquals("categories", firstSection.name)
        assertTrue(firstSection.items.isNotEmpty())

        // Verify second section (restaurants)
        val restaurantSection = response.sections[1]
        assertEquals("restaurants", restaurantSection.name)
        assertEquals(2, restaurantSection.items.size)

        // Verify first restaurant
        val firstRestaurant = restaurantSection.items[0]
        assertNotNull(firstRestaurant.venue)
        assertEquals("5ae6013cf78b5a000bb64022", firstRestaurant.venue?.id)
        assertEquals("McDonald's Helsinki Kamppi", firstRestaurant.venue?.name)
        assertEquals("I'm lovin' it.", firstRestaurant.venue?.shortDescription)
        assertEquals("U7DI{Zj@0Uj@s-fQ9za|0Caz},fQWEfQ^Kjt", firstRestaurant.image.blurhash)

        // Verify request was made with correct parameters
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/v1/pages/restaurants?lat=60.17&lon=24.94", recordedRequest.path)
        assertEquals("GET", recordedRequest.method)
    }

    @Test
    fun `DTO to Domain mapping works correctly`() = runTest {
        // Given: Valid API response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(VALID_RESPONSE_JSON)

        mockWebServer.enqueue(mockResponse)

        // When: API call is made and mapped to domain
        val response = api.getRestaurants(latitude = 60.17, longitude = 24.94)
        val restaurantItems = response.sections[1].items
        val venues = restaurantItems.map { it.toDomain(isFavourite = false) }

        // Then: Domain models are created correctly
        assertEquals(2, venues.size)

        val firstVenue = venues[0]
        assertEquals("5ae6013cf78b5a000bb64022", firstVenue.id)
        assertEquals("McDonald's Helsinki Kamppi", firstVenue.name)
        assertEquals("I'm lovin' it.", firstVenue.description)
        assertEquals("U7DI{Zj@0Uj@s-fQ9za|0Caz},fQWEfQ^Kjt", firstVenue.blurHash)
        assertEquals("https://imageproxy.wolt.com/mes-image/8695de58-c638-437d-a314-ad0ee5bc530f/2fa31f49-7a63-455e-999d-6b470d22903a", firstVenue.imageUrl)
        assertEquals(false, firstVenue.isFavourite)

        val secondVenue = venues[1]
        assertEquals("62bc5c0d4a41e8af8002f3fd", secondVenue.id)
        assertEquals("Noodle Story Freda", secondVenue.name)
        assertNull(secondVenue.description) // No short_description in this venue
        assertEquals(false, secondVenue.isFavourite)
    }

    @Test
    fun `favourite status is preserved in mapping`() = runTest {
        // Given: Valid API response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(VALID_RESPONSE_JSON)

        mockWebServer.enqueue(mockResponse)

        // When: API call is made and mapped with favourite status
        val response = api.getRestaurants(latitude = 60.17, longitude = 24.94)
        val restaurantItems = response.sections[1].items
        val favouriteIds = setOf("5ae6013cf78b5a000bb64022")

        val venues = restaurantItems.map { item ->
            item.toDomain(isFavourite = favouriteIds.contains(item.venue?.id))
        }

        // Then: Favourite status is correct
        val firstVenue = venues.find { it.id == "5ae6013cf78b5a000bb64022" }
        assertNotNull(firstVenue)
        assertEquals(true, firstVenue?.isFavourite)

        val secondVenue = venues.find { it.id == "62bc5c0d4a41e8af8002f3fd" }
        assertNotNull(secondVenue)
        assertEquals(false, secondVenue?.isFavourite)
    }

    @Test
    fun `getRestaurants handles server error 500`() {
        // Given: Mock server returns 500 error
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .setBody("{\"error\": \"Internal Server Error\"}")

        mockWebServer.enqueue(mockResponse)

        // When & Then: ErrorInterceptor transforms to ServerException
        val exception = assertThrows(ServerException::class.java) {
            runTest {
                api.getRestaurants(latitude = 60.17, longitude = 24.94)
            }
        }

        assertEquals(500, exception.code)
        assertTrue(exception.message?.contains("Server error") == true)
    }

    @Test
    fun `getRestaurants handles malformed JSON`() {
        // Given: Mock server returns malformed JSON
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{invalid json")

        mockWebServer.enqueue(mockResponse)

        // When & Then: API call throws exception
        assertThrows(Exception::class.java) {
            runTest {
                api.getRestaurants(latitude = 60.17, longitude = 24.94)
            }
        }
    }

    @Test
    fun `getRestaurants handles missing venue data`() = runTest {
        // Given: Mock server returns response with missing venue data
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(RESPONSE_WITH_MISSING_VENUE)

        mockWebServer.enqueue(mockResponse)

        // When: API call is made
        val response = api.getRestaurants(latitude = 60.17, longitude = 24.94)

        // Then: Response contains sections
        assertEquals(2, response.sections.size)

        val restaurantSection = response.sections[1]
        assertEquals("restaurants", restaurantSection.name)

        // Items with null venue should fail when mapping to domain
        val itemWithNullVenue = restaurantSection.items[0]
        assertNull(itemWithNullVenue.venue)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            itemWithNullVenue.toDomain()
        }

        assertTrue(exception.message?.contains("RestaurantItemDto must have venue") == true)
    }

    @Test
    fun `getRestaurants handles empty sections`() = runTest {
        // Given: Mock server returns response with empty sections
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(RESPONSE_WITH_EMPTY_SECTIONS)

        mockWebServer.enqueue(mockResponse)

        // When: API call is made
        val response = api.getRestaurants(latitude = 60.17, longitude = 24.94)

        // Then: Response has empty sections
        assertTrue(response.sections.isEmpty())
    }

    @Test
    fun `getRestaurants handles network timeout`() {
        // Given: Mock server delays response longer than timeout
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(VALID_RESPONSE_JSON)
            .setBodyDelay(2, TimeUnit.SECONDS) // Longer than 1s timeout

        mockWebServer.enqueue(mockResponse)

        // When & Then: API call throws timeout exception
        assertThrows(Exception::class.java) {
            runTest {
                api.getRestaurants(latitude = 60.17, longitude = 24.94)
            }
        }
    }

    companion object {
        private const val VALID_RESPONSE_JSON = """
        {
          "sections": [
            {
              "name": "categories",
              "items": [
                {
                  "image": {
                    "blurhash": "j8WJ:YKp;:lBTsGXlmlk;;qG8iKG",
                    "url": "https://discovery-cdn.wolt.com/categories/a69b5aea-c5a8-11ea-9f48-2e3b484a03e4_0b2c3eb5_ae95_4bff_9144_7f7c93ea74f9.jpg-md"
                  }
                }
              ]
            },
            {
              "name": "restaurants",
              "items": [
                {
                  "image": {
                    "blurhash": "U7DI{Zj@0Uj@s-fQ9za|0Caz},fQWEfQ^Kjt",
                    "url": "https://imageproxy.wolt.com/mes-image/8695de58-c638-437d-a314-ad0ee5bc530f/2fa31f49-7a63-455e-999d-6b470d22903a"
                  },
                  "venue": {
                    "id": "5ae6013cf78b5a000bb64022",
                    "name": "McDonald's Helsinki Kamppi",
                    "short_description": "I'm lovin' it."
                  }
                },
                {
                  "image": {
                    "blurhash": "j4zE5H;K;;8R8hl34ih5PacQ;2l4",
                    "url": "https://imageproxy.wolt.com/assets/68399adef56825c5193ac465"
                  },
                  "venue": {
                    "id": "62bc5c0d4a41e8af8002f3fd",
                    "name": "Noodle Story Freda"
                  }
                }
              ]
            }
          ]
        }
        """

        private const val RESPONSE_WITH_MISSING_VENUE = """
        {
          "sections": [
            {
              "name": "categories",
              "items": []
            },
            {
              "name": "restaurants",
              "items": [
                {
                  "image": {
                    "blurhash": "U7DI{Zj@0Uj@s-fQ9za|0Caz},fQWEfQ^Kjt",
                    "url": "https://imageproxy.wolt.com/mes-image/8695de58-c638-437d-a314-ad0ee5bc530f/2fa31f49-7a63-455e-999d-6b470d22903a"
                  }
                }
              ]
            }
          ]
        }
        """

        private const val RESPONSE_WITH_EMPTY_SECTIONS = """
        {
          "sections": []
        }
        """
    }
}
