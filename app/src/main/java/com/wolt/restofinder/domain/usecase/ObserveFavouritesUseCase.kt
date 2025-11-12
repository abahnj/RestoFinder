package com.wolt.restofinder.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveFavouritesUseCase @Inject constructor(
    // Will inject FavouritesDataStore when implemented
) {

    /**
     * Observe the set of favourite venue IDs.
     *
     * @return Flow emitting Set<String> of favourite venue IDs
     */
    operator fun invoke(): Flow<Set<String>> {
        // Implementation will be added when FavouritesDataStore is created
        return flowOf(emptySet())
    }
}
