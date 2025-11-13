package com.wolt.restofinder.data.location

import app.cash.turbine.test
import com.wolt.restofinder.data.location.LocationRepositoryImpl.Companion.COORDINATES
import com.wolt.restofinder.domain.model.Location
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationRepositoryImplTest {

    private val repository = LocationRepositoryImpl()

    @Test
    fun `getLocationUpdates emits first location immediately`() = runTest {
        repository.getLocationUpdates(delayMillis = 10).test {
            val firstLocation = awaitItem()
            assertEquals(Location(60.169418, 24.931618), firstLocation)
            cancel()
        }
    }

    @Test
    fun `getLocationUpdates cycles through all 9 coordinates`() = runTest {
        repository.getLocationUpdates(delayMillis = 10).test {
            val actual = List(9) { awaitItem() }
            assertEquals(COORDINATES, actual)

            cancel()
        }
    }

    @Test
    fun `getLocationUpdates loops back to first after 9th`() = runTest {
        repository.getLocationUpdates(delayMillis = 10).test {
            repeat(9) { awaitItem() }

            val tenthLocation = awaitItem()
            assertEquals(COORDINATES[0], tenthLocation)

            cancel()
        }
    }

    @Test
    fun `coordinates match assignment specification`() {
        assertEquals(Location(60.169418, 24.931618), COORDINATES[0])
        assertEquals(9, COORDINATES.size)
    }
}

