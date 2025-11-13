package com.wolt.restofinder.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FavouritesDataStoreTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private fun createDataStore(scope: kotlinx.coroutines.CoroutineScope): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { tmpFolder.newFile("test.preferences_pb") }
        )
    }

    @Test
    fun `favouritesFlow emits initial empty set`() = runTest {
        val dataStore = FavouritesDataStore(createDataStore(backgroundScope))

        dataStore.favouritesFlow.test {
            assertEquals(emptySet<String>(), awaitItem())
            cancel()
        }
    }

    @Test
    fun `toggleFavourite adds venue to favourites`() = runTest {
        val dataStore = FavouritesDataStore(createDataStore(backgroundScope))

        dataStore.favouritesFlow.test {
            assertEquals(emptySet<String>(), awaitItem())

            dataStore.toggleFavourite("venue1")
            assertEquals(setOf("venue1"), awaitItem())

            cancel()
        }
    }

    @Test
    fun `toggleFavourite removes venue from favourites`() = runTest {
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
    fun `favouritesFlow emits updates on toggle`() = runTest {
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
    fun `isFavourite returns correct status`() = runTest {
        val dataStore = FavouritesDataStore(createDataStore(backgroundScope))

        assertEquals(false, dataStore.isFavourite("venue1"))

        dataStore.toggleFavourite("venue1")
        assertEquals(true, dataStore.isFavourite("venue1"))

        dataStore.toggleFavourite("venue1")
        assertEquals(false, dataStore.isFavourite("venue1"))
    }
}
