package com.wolt.restofinder.domain.repository

import com.wolt.restofinder.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    /**
     * Get a flow of location updates.
     * Emits 9 predefined Helsinki coordinates at specified interval.
     */
    fun getLocationUpdates(delayMillis: Long = 10_000): Flow<Location>
}
