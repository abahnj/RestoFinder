package com.wolt.restofinder.domain.usecase

import com.wolt.restofinder.domain.repository.VenueRepository
import javax.inject.Inject

class ToggleFavouriteUseCase @Inject constructor(
    private val venueRepository: VenueRepository
) {

    suspend operator fun invoke(venueId: String): Result<Unit> = venueRepository.toggleFavourite(venueId)
}
