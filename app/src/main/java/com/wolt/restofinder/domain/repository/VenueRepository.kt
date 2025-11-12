package com.wolt.restofinder.domain.repository

import com.wolt.restofinder.domain.model.Venue
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for venue/restaurant data operations.
 */
interface VenueRepository {

    /**
     * Get a flow of nearby venues for the given location.
     *
     * Emits Result.success with venue list on successful fetch.
     * Emits Result.failure with exception on network errors.
     */
    fun getNearbyVenues(latitude: Double, longitude: Double): Flow<Result<List<Venue>>>
}
