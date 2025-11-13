package com.wolt.restofinder.domain.usecase

import com.wolt.restofinder.domain.model.Location
import com.wolt.restofinder.domain.model.Venue
import com.wolt.restofinder.domain.repository.VenueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNearbyVenuesUseCase @Inject constructor(
    private val venueRepository: VenueRepository
) {

    /**
     * Get up to 15 nearby venues for the location.
     */
    operator fun invoke(location: Location): Flow<Result<List<Venue>>> =
        venueRepository.observeNearbyVenuesWithFavourites(location.latitude, location.longitude)
            .map { result ->
                result.map { venues ->
                    venues.take(15) // Business rule: limit to 15 venues
                }
            }
}
