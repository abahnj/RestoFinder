package com.wolt.restofinder.presentation.venues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolt.restofinder.domain.model.Location
import com.wolt.restofinder.domain.usecase.GetNearbyVenuesUseCase
import com.wolt.restofinder.domain.usecase.ObserveLocationUpdatesUseCase
import com.wolt.restofinder.domain.usecase.ToggleFavouriteUseCase
import com.wolt.restofinder.presentation.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
open class VenueListViewModel
    @Inject
    constructor(
        private val getNearbyVenuesUseCase: GetNearbyVenuesUseCase,
        private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
        private val observeLocationUpdatesUseCase: ObserveLocationUpdatesUseCase,
    ) : ViewModel() {
        private var currentLocation: Location? = null
        private val retryTrigger = MutableSharedFlow<Unit>()

        protected val _uiState = MutableStateFlow<VenueListUiState>(VenueListUiState.Loading)
        val uiState: StateFlow<VenueListUiState> = _uiState.asStateFlow()

        private val _currentLocation = MutableStateFlow<Location?>(null)
        val currentLocationFlow: StateFlow<Location?> = _currentLocation.asStateFlow()

        private val _events = MutableSharedFlow<UiEvent>()
        val events = _events.asSharedFlow()

        init {
            observeLocationUpdates()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        private fun observeLocationUpdates() {
            viewModelScope.launch {
                merge(
                    observeLocationUpdatesUseCase()
                        .onEach { location ->
                            currentLocation = location
                            _currentLocation.value = location
                            Timber.d("Location emitted: ${location.latitude}, ${location.longitude}")
                        },
                    retryTrigger.map {
                        currentLocation ?: Location(0.0, 0.0)
                    },
                )
                    .flatMapLatest { location ->
                        fetchVenues(location)
                    }
                    .collect { state ->
                        _uiState.value = state
                    }
            }
        }

        private fun fetchVenues(location: Location): Flow<VenueListUiState> {
            return getNearbyVenuesUseCase(location)
                .map { result ->
                    result.fold(
                        onSuccess = { venues ->
                            Timber.i("Loaded ${venues.size} venues")
                            VenueListUiState.Success(venues, location)
                        },
                        onFailure = { exception ->
                            Timber.w(exception, "Failed to load venues")
                            VenueListUiState.Error(
                                message = exception.message ?: "Failed to load venues",
                            )
                        },
                    )
                }
                .onStart {
                    Timber.d("Loading venues...")
                    emit(VenueListUiState.Loading)
                }
                .catch { exception ->
                    Timber.e(exception, "Unexpected error in venue flow")
                    emit(
                        VenueListUiState.Error(
                            message = "Unexpected error: ${exception.message}",
                        ),
                    )
                }
        }

        open fun toggleFavourite(venueId: String) {
            viewModelScope.launch {
                val currentState = _uiState.value
                if (currentState is VenueListUiState.Success) {
                    // Optimistic update - update UI immediately
                    val venue = currentState.venues.find { it.id == venueId }
                    val wasFavourite = venue?.isFavourite ?: false

                    // Perform actual toggle
                    val result = toggleFavouriteUseCase(venueId)

                    result.onSuccess {
                        // Show success feedback with undo
                        _events.emit(
                            UiEvent.ShowSnackbar(
                                message = if (!wasFavourite) "Added to favourites" else "Removed from favourites",
                                actionLabel = "Undo",
                                onAction = { toggleFavourite(venueId) },
                            ),
                        )
                    }

                    result.onFailure { exception ->
                        Timber.e(exception, "Failed to toggle favourite")
                        _events.emit(UiEvent.ShowSnackbar("Failed to update favourite"))
                    }
                }
            }
        }

        open fun retry() {
            viewModelScope.launch {
                Timber.d("Retrying venue fetch at current location")
                retryTrigger.emit(Unit)
            }
        }
    }
