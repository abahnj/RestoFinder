package com.wolt.restofinder.domain.repository

import com.wolt.restofinder.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    /**
     * Get a flow of location updates.
     * Emits 9 predefined Helsinki coordinates every 10 seconds.
     */
    fun getLocationUpdates(): Flow<Location>
}
