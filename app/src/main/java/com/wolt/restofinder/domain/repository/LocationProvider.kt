package com.wolt.restofinder.domain.repository

import com.wolt.restofinder.domain.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * Provider interface for location updates.
 */
interface LocationProvider {

    /**
     * Get a flow of location updates.
     *
     * For this assignment, this returns a Flow that:
     * - Emits 9 predefined Helsinki coordinates
     * - Updates every 10 seconds
     * - Loops back to the first coordinate after the 9th
     *
     * @return Flow emitting Location updates every 10 seconds
     */
    fun getLocationUpdates(): Flow<Location>
}
