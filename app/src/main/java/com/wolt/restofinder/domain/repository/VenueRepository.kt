package com.wolt.restofinder.domain.repository

import com.wolt.restofinder.domain.model.Venue
import kotlinx.coroutines.flow.Flow

interface VenueRepository {

    /**
     * Observe nearby venues with favourite status merged.
     * Emits updated list when favourites change.
     */
    fun observeNearbyVenuesWithFavourites(latitude: Double, longitude: Double): Flow<Result<List<Venue>>>

    /**
     * Toggle favourite status for a venue.
     */
    suspend fun toggleFavourite(venueId: String): Result<Unit>
}
