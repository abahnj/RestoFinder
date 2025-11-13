package com.wolt.restofinder.presentation.venues

import com.wolt.restofinder.domain.model.Location
import com.wolt.restofinder.domain.model.Venue

sealed class VenueListUiState {
    data object Loading : VenueListUiState()

    data class Success(
        val venues: List<Venue>,
        val currentLocation: Location
    ) : VenueListUiState()

    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : VenueListUiState()
}
