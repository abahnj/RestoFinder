package com.wolt.restofinder.domain.usecase

import app.cash.turbine.test
import com.wolt.restofinder.domain.model.Location
import com.wolt.restofinder.domain.model.Venue
import com.wolt.restofinder.domain.repository.VenueRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNearbyVenuesUseCaseTest {

    private val mockRepository = mockk<VenueRepository>()
    private val useCase = GetNearbyVenuesUseCase(mockRepository)

    private val testLocation = Location(60.17, 24.93)

    @Test
    fun `invoke limits venues to 15 maximum`() = runTest {
        val venues = (1..20).map { createVenue(it.toString()) }
        every { mockRepository.observeNearbyVenuesWithFavourites(any(), any()) } returns
            flowOf(Result.success(venues))

        useCase(testLocation).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(15, result.getOrNull()?.size)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns all venues when fewer than 15`() = runTest {
        val venues = (1..10).map { createVenue(it.toString()) }
        every { mockRepository.observeNearbyVenuesWithFavourites(any(), any()) } returns
            flowOf(Result.success(venues))

        useCase(testLocation).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(10, result.getOrNull()?.size)
            awaitComplete()
        }
    }

    @Test
    fun `invoke preserves venue order from repository`() = runTest {
        val venues = listOf(
            createVenue("1"),
            createVenue("2"),
            createVenue("3")
        )
        every { mockRepository.observeNearbyVenuesWithFavourites(any(), any()) } returns
            flowOf(Result.success(venues))

        useCase(testLocation).test {
            val result = awaitItem()
            assertEquals(venues, result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `invoke propagates errors from repository`() = runTest {
        val exception = RuntimeException("Network error")
        every { mockRepository.observeNearbyVenuesWithFavourites(any(), any()) } returns
            flowOf(Result.failure(exception))

        useCase(testLocation).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            awaitComplete()
        }
    }

    private fun createVenue(id: String) = Venue(
        id = id,
        name = "Venue $id",
        description = "Description $id",
        blurHash = "hash$id",
        imageUrl = "https://example.com/$id.jpg",
        isFavourite = false
    )
}
