package com.wolt.restofinder.domain.usecase

import app.cash.turbine.test
import com.wolt.restofinder.domain.model.Location
import com.wolt.restofinder.domain.repository.LocationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveLocationUpdatesUseCaseTest {
    private val mockRepository = mockk<LocationRepository>()
    private val useCase = ObserveLocationUpdatesUseCase(mockRepository)

    private val testLocation = Location(60.17, 24.93)

    @Test
    fun `invoke delegates to repository with default delay`() =
        runTest {
            every { mockRepository.getLocationUpdates(10_000) } returns flowOf(testLocation)

            useCase().test {
                assertEquals(testLocation, awaitItem())
                awaitComplete()
            }

            verify { mockRepository.getLocationUpdates(10_000) }
        }

    @Test
    fun `invoke delegates to repository with custom delay`() =
        runTest {
            every { mockRepository.getLocationUpdates(5_000) } returns flowOf(testLocation)

            useCase(5_000).test {
                assertEquals(testLocation, awaitItem())
                awaitComplete()
            }

            verify { mockRepository.getLocationUpdates(5_000) }
        }
}
