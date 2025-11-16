package com.wolt.restofinder.domain.usecase

import com.wolt.restofinder.domain.model.Location
import com.wolt.restofinder.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLocationUpdatesUseCase
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
    ) {
        operator fun invoke(delayMillis: Long = 10_000): Flow<Location> = locationRepository.getLocationUpdates(delayMillis)
    }
