package com.wolt.restofinder.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class FavouritesDataStoreIntegrationTest {

    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var testScope: TestScope
    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var favouritesDataStore: FavouritesDataStore
    private lateinit var testDataStoreFile: File

    @Before
    fun setup() {
        // Use unique filename per test to avoid DataStore singleton conflicts
        val uniqueFileName = "test_favourites_${System.nanoTime()}.preferences_pb"
        testDataStoreFile = testContext.preferencesDataStoreFile(uniqueFileName)

        testScope = TestScope(testDispatcher + Job())

        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testDataStoreFile }
        )

        favouritesDataStore = FavouritesDataStore(testDataStore)
    }

    @After
    fun cleanup() {
        runBlocking {
            testScope.cancel()
            testScope.testScheduler.advanceUntilIdle()
        }

        // Delete the specific test file
        if (testDataStoreFile.exists()) {
            testDataStoreFile.delete()
        }
    }

    @Test
    fun toggleFavourite_addsThenRemoves() = runTest {
        favouritesDataStore.toggleFavourite("venue1")
        assertTrue(favouritesDataStore.isFavourite("venue1"))

        favouritesDataStore.toggleFavourite("venue1")
        assertFalse(favouritesDataStore.isFavourite("venue1"))
    }

    @Test
    fun isFavourite_returnsCorrectStatus() = runTest {
        favouritesDataStore.toggleFavourite("venue1")
        favouritesDataStore.toggleFavourite("venue2")

        assertTrue(favouritesDataStore.isFavourite("venue1"))
        assertTrue(favouritesDataStore.isFavourite("venue2"))
        assertFalse(favouritesDataStore.isFavourite("venue3"))
    }

    @Test
    fun favouritesFlow_emitsUpdatesReactively() = runTest {
        favouritesDataStore.favouritesFlow.test {
            // Initial state - empty
            assertEquals(emptySet<String>(), awaitItem())

            // Toggle to add venue1
            favouritesDataStore.toggleFavourite("venue1")
            assertEquals(setOf("venue1"), awaitItem())

            // Toggle to add venue2
            favouritesDataStore.toggleFavourite("venue2")
            assertEquals(setOf("venue1", "venue2"), awaitItem())

            // Toggle to remove venue1
            favouritesDataStore.toggleFavourite("venue1")
            assertEquals(setOf("venue2"), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun multipleToggles_maintainConsistentState() = runTest {
        val venueId = "venue1"

        // Toggle multiple times
        favouritesDataStore.toggleFavourite(venueId)
        favouritesDataStore.toggleFavourite(venueId)
        favouritesDataStore.toggleFavourite(venueId)

        // Should be in favourites (odd number of toggles)
        assertTrue(favouritesDataStore.isFavourite(venueId))

        // Toggle one more time
        favouritesDataStore.toggleFavourite(venueId)

        // Should not be in favourites (even number of toggles)
        assertFalse(favouritesDataStore.isFavourite(venueId))
    }

    @Test
    fun favouritesFlow_persistsAcrossReads() = runTest {
        favouritesDataStore.toggleFavourite("venue1")
        favouritesDataStore.toggleFavourite("venue2")

        favouritesDataStore.favouritesFlow.test {
            val favourites = awaitItem()
            assertEquals(setOf("venue1", "venue2"), favourites)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
