package com.wolt.restofinder.domain.repository

import com.wolt.restofinder.domain.model.Venue
import kotlinx.coroutines.flow.Flow

interface VenueRepository {

    /**
     * Get nearby venues for the location.
     * Emits updated list when favourites change.
     */
    fun getNearbyVenues(latitude: Double, longitude: Double): Flow<Result<List<Venue>>>

    /**
     * Toggle favourite status for a venue.
     */
    suspend fun toggleFavourite(venueId: String): Result<Unit>
}
