package com.wolt.restofinder.domain.usecase

import com.wolt.restofinder.domain.repository.VenueRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ToggleFavouriteUseCaseTest {

    private val mockRepository = mockk<VenueRepository>()
    private val useCase = ToggleFavouriteUseCase(mockRepository)

    @Test
    fun `invoke delegates to repository`() = runTest {
        coEvery { mockRepository.toggleFavourite(any()) } returns Result.success(Unit)

        val result = useCase("venue1")

        assertTrue(result.isSuccess)
        coVerify { mockRepository.toggleFavourite("venue1") }
    }

    @Test
    fun `invoke propagates success from repository`() = runTest {
        coEvery { mockRepository.toggleFavourite("venue1") } returns Result.success(Unit)

        val result = useCase("venue1")

        assertEquals(Result.success(Unit), result)
    }

    @Test
    fun `invoke propagates errors from repository`() = runTest {
        val exception = IOException("DataStore error")
        coEvery { mockRepository.toggleFavourite("venue1") } returns Result.failure(exception)

        val result = useCase("venue1")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
