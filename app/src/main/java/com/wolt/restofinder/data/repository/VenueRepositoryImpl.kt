package com.wolt.restofinder.data.repository

import com.wolt.restofinder.data.local.FavouritesDataStore
import com.wolt.restofinder.data.mapper.toDomain
import com.wolt.restofinder.data.remote.api.WoltApi
import com.wolt.restofinder.domain.model.Venue
import com.wolt.restofinder.domain.repository.VenueRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VenueRepositoryImpl @Inject constructor(
    private val api: WoltApi,
    private val favouritesDataStore: FavouritesDataStore
) : VenueRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNearbyVenuesWithFavourites(latitude: Double, longitude: Double): Flow<Result<List<Venue>>> =
        fetchVenues(latitude, longitude)
            .flatMapLatest { restaurantItems ->
                getFavouritesFlow().map { favouriteIds ->
                    Result.success(
                        restaurantItems.map { item ->
                            item.toDomain(isFavourite = favouriteIds.contains(item.venue.id))
                        }
                    )
                }
            }
            .catch { e ->
                Timber.w(e, "Failed to fetch venues")
                emit(Result.failure(e))
            }

    private fun fetchVenues(latitude: Double, longitude: Double) = flow {
        Timber.d("Fetching venues for ($latitude, $longitude)")

        val response = withTimeout(10_000) {
            api.getRestaurants(latitude, longitude)
        }

        // TODO: Production - Add robust section parsing
        // 1. Add 'name' field back to SectionDto
        // 2. Find section by name: sections.find { it.name.contains("restaurant") }
        // 3. Fallback to sections.getOrNull(1) if not found
        // 4. Throw clear error if no section found
        // This makes code resilient to API changes (section order, naming)
        val restaurantItems = response.sections[1].items
        Timber.i("Successfully fetched ${restaurantItems.size} venues")

        emit(restaurantItems)
    }

    private fun getFavouritesFlow(): Flow<Set<String>> = favouritesDataStore.favouritesFlow

    override suspend fun toggleFavourite(venueId: String): Result<Unit> = try {
        favouritesDataStore.toggleFavourite(venueId)
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Failed to toggle favourite for $venueId")
        Result.failure(e)
    }
}
