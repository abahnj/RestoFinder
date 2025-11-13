package com.wolt.restofinder.data.location

import com.wolt.restofinder.domain.model.Location
import com.wolt.restofinder.domain.repository.LocationRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor() : LocationRepository {

    override fun getLocationUpdates(delayMillis: Long): Flow<Location> = flow {
        var index = 0
        while (currentCoroutineContext().isActive) {
            val location = COORDINATES[index]
            Timber.d("Emitting location ${index + 1}/9: ${location.latitude}, ${location.longitude}")
            emit(location)

            index = (index + 1) % COORDINATES.size
            delay(delayMillis)
        }
    }

    companion object {
        val COORDINATES = listOf(
            Location(60.169418, 24.931618),
            Location(60.169818, 24.932906),
            Location(60.170005, 24.935105),
            Location(60.169108, 24.936210),
            Location(60.168355, 24.934869),
            Location(60.167560, 24.932562),
            Location(60.168254, 24.931532),
            Location(60.169012, 24.930341),
            Location(60.170085, 24.929569)
        )
    }
}
