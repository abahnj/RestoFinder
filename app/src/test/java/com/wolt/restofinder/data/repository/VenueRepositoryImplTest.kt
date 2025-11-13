package com.wolt.restofinder.data.repository

import app.cash.turbine.test
import com.wolt.restofinder.data.local.FavouritesDataStore
import com.wolt.restofinder.data.remote.api.WoltApi
import com.wolt.restofinder.data.remote.dto.ImageDto
import com.wolt.restofinder.data.remote.dto.RestaurantItemDto
import com.wolt.restofinder.data.remote.dto.SectionDto
import com.wolt.restofinder.data.remote.dto.VenueDetailsDto
import com.wolt.restofinder.data.remote.dto.WoltApiResponseDto
import com.wolt.restofinder.domain.model.Venue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class VenueRepositoryImplTest {

    private val mockApi = mockk<WoltApi>()
    private val mockDataStore = mockk<FavouritesDataStore>(relaxed = true)
    private val repository = VenueRepositoryImpl(mockApi, mockDataStore)

    @Test
    fun `observeNearbyVenuesWithFavourites successfully fetches and merges`() = runTest {
        val mockResponse = createMockResponse()
        coEvery { mockApi.getRestaurants(any(), any()) } returns mockResponse
        every { mockDataStore.favouritesFlow } returns flowOf(setOf("venue1"))

        repository.observeNearbyVenuesWithFavourites(60.17, 24.93).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)

            val venues = result.getOrNull()!!
            assertEquals(2, venues.size)
            assertEquals(true, venues[0].isFavourite)  // venue1 is favourite
            assertEquals(false, venues[1].isFavourite) // venue2 is not

            awaitComplete()
        }
    }

    @Test
    fun `favourite toggle triggers re-emission without network refetch`() = runTest {
        val mockResponse = createMockResponse()
        coEvery { mockApi.getRestaurants(any(), any()) } returns mockResponse

        val favouritesFlow = kotlinx.coroutines.flow.MutableStateFlow(emptySet<String>())
        every { mockDataStore.favouritesFlow } returns favouritesFlow

        repository.observeNearbyVenuesWithFavourites(60.17, 24.93).test {
            // Initial emission
            val first = awaitItem()
            assertEquals(false, first.getOrNull()!![0].isFavourite)

            // Toggle favourite
            favouritesFlow.value = setOf("venue1")

            // Should re-emit without network refetch
            val second = awaitItem()
            assertEquals(true, second.getOrNull()!![0].isFavourite)

            // Verify API called only ONCE
            coVerify(exactly = 1) { mockApi.getRestaurants(any(), any()) }

            cancel()
        }
    }

    @Test
    fun `observeNearbyVenuesWithFavourites handles network errors`() = runTest {
        coEvery { mockApi.getRestaurants(any(), any()) } throws IOException("Network error")
        every { mockDataStore.favouritesFlow } returns flowOf(emptySet())

        repository.observeNearbyVenuesWithFavourites(60.17, 24.93).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IOException)

            awaitComplete()
        }
    }

    @Test
    fun `toggleFavourite delegates to DataStore`() = runTest {
        coEvery { mockDataStore.toggleFavourite(any()) } returns Unit

        val result = repository.toggleFavourite("venue1")

        assertTrue(result.isSuccess)
        coVerify { mockDataStore.toggleFavourite("venue1") }
    }

    @Test
    fun `toggleFavourite handles errors`() = runTest {
        coEvery { mockDataStore.toggleFavourite(any()) } throws IOException("DataStore error")

        val result = repository.toggleFavourite("venue1")

        assertTrue(result.isFailure)
    }

    private fun createMockResponse() = WoltApiResponseDto(
        sections = listOf(
            SectionDto(items = emptyList()), // Categories section
            SectionDto(
                items = listOf(
                    RestaurantItemDto(
                        image = ImageDto("https://example.com/1.jpg", "hash1"),
                        venue = VenueDetailsDto("venue1", "Restaurant 1", "Description 1")
                    ),
                    RestaurantItemDto(
                        image = ImageDto("https://example.com/2.jpg", "hash2"),
                        venue = VenueDetailsDto("venue2", "Restaurant 2", null)
                    )
                )
            )
        )
    )
}
