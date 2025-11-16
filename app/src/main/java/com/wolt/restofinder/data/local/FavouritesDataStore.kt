package com.wolt.restofinder.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesDataStore
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        private object Keys {
            val FAVOURITES = stringSetPreferencesKey("favourite_venue_ids")
        }

        val favouritesFlow: Flow<Set<String>> =
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        Timber.e(exception, "Error reading favourites from DataStore")
                        emit(androidx.datastore.preferences.core.emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[Keys.FAVOURITES] ?: emptySet()
                }

        suspend fun toggleFavourite(venueId: String) {
            dataStore.edit { preferences ->
                val currentFavourites = preferences[Keys.FAVOURITES]?.toMutableSet() ?: mutableSetOf()

                if (currentFavourites.contains(venueId)) {
                    currentFavourites.remove(venueId)
                    Timber.d("Removed $venueId from favourites")
                } else {
                    currentFavourites.add(venueId)
                    Timber.d("Added $venueId to favourites")
                }

                preferences[Keys.FAVOURITES] = currentFavourites
            }
        }

        suspend fun isFavourite(venueId: String): Boolean {
            return dataStore.data.map { preferences ->
                preferences[Keys.FAVOURITES]?.contains(venueId) ?: false
            }.catch { emit(false) }.first()
        }
    }
