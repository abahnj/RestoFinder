package com.wolt.restofinder.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.IOException

class FavouritesDataStoreTest {
    @get:Rule
    val tmpFolder = TemporaryFolder()

    private fun createDataStore(scope: kotlinx.coroutines.CoroutineScope): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { tmpFolder.newFile("test.preferences_pb") },
        )
    }

    @Test
    fun `favouritesFlow emits initial empty set`() =
        runTest {
            val dataStore = FavouritesDataStore(createDataStore(backgroundScope))

            dataStore.favouritesFlow.test {
                assertEquals(emptySet<String>(), awaitItem())
                cancel()
            }
        }

    @Test
    fun `toggleFavourite adds venue to favourites`() =
        runTest {
            val dataStore = FavouritesDataStore(createDataStore(backgroundScope))

            dataStore.favouritesFlow.test {
                assertEquals(emptySet<String>(), awaitItem())

                dataStore.toggleFavourite("venue1")
                assertEquals(setOf("venue1"), awaitItem())

                cancel()
            }
        }

    @Test
    fun `toggleFavourite removes venue from favourites`() =
        runTest {
            val dataStore = FavouritesDataStore(createDataStore(backgroundScope))

            dataStore.toggleFavourite("venue1")

            dataStore.favouritesFlow.test {
                assertEquals(setOf("venue1"), awaitItem())

                dataStore.toggleFavourite("venue1")
                assertEquals(emptySet<String>(), awaitItem())

                cancel()
            }
        }

    @Test
    fun `favouritesFlow emits updates on toggle`() =
        runTest {
            val dataStore = FavouritesDataStore(createDataStore(backgroundScope))

            dataStore.favouritesFlow.test {
                assertEquals(emptySet<String>(), awaitItem())

                dataStore.toggleFavourite("venue1")
                assertEquals(setOf("venue1"), awaitItem())

                dataStore.toggleFavourite("venue2")
                assertEquals(setOf("venue1", "venue2"), awaitItem())

                dataStore.toggleFavourite("venue1")
                assertEquals(setOf("venue2"), awaitItem())

                cancel()
            }
        }

    @Test
    fun `isFavourite returns correct status`() =
        runTest {
            val dataStore = FavouritesDataStore(createDataStore(backgroundScope))

            assertEquals(false, dataStore.isFavourite("venue1"))

            dataStore.toggleFavourite("venue1")
            assertEquals(true, dataStore.isFavourite("venue1"))

            dataStore.toggleFavourite("venue1")
            assertEquals(false, dataStore.isFavourite("venue1"))
        }

    @Test
    fun `favouritesFlow handles IOException by emitting empty set`() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            every { mockDataStore.data } returns flow { throw IOException("Simulated corruption") }

            val dataStore = FavouritesDataStore(mockDataStore)

            dataStore.favouritesFlow.test {
                assertTrue(awaitItem().isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `favouritesFlow handles various IOException scenarios`() =
        runTest {
            val errors = listOf("File corrupted", "Read error", "Invalid format")

            for (errorMsg in errors) {
                val mockDataStore = mockk<DataStore<Preferences>>()
                every { mockDataStore.data } returns flow { throw IOException(errorMsg) }

                val dataStore = FavouritesDataStore(mockDataStore)

                dataStore.favouritesFlow.test {
                    assertTrue("Should handle $errorMsg", awaitItem().isEmpty())
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

    @Test
    fun `isFavourite handles IOException and returns false`() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            every { mockDataStore.data } returns flow { throw IOException("Corrupted") }

            val dataStore = FavouritesDataStore(mockDataStore)

            assertFalse(dataStore.isFavourite("venue1"))
        }

    @Test
    fun `favouritesFlow handles empty preferences`() =
        runTest {
            val mockPreferences = mockk<Preferences>()
            val mockDataStore = mockk<DataStore<Preferences>>()

            every { mockPreferences[any<Preferences.Key<Set<String>>>()] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val dataStore = FavouritesDataStore(mockDataStore)

            dataStore.favouritesFlow.test {
                assertTrue(awaitItem().isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `favouritesFlow maps preferences correctly`() =
        runTest {
            val mockPreferences = mockk<Preferences>()
            val mockDataStore = mockk<DataStore<Preferences>>()

            val expected = setOf("venue1", "venue2")
            every { mockPreferences[any<Preferences.Key<Set<String>>>()] } returns expected
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val dataStore = FavouritesDataStore(mockDataStore)

            dataStore.favouritesFlow.test {
                assertEquals(expected, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
}
