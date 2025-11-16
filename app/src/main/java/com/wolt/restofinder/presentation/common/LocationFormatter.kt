package com.wolt.restofinder.presentation.common

import com.wolt.restofinder.domain.model.Location
import kotlin.math.abs

/**
 * Formats Location coordinates to Helsinki street addresses.
 * Maps the 9 hardcoded coordinates to known Helsinki locations.
 */
fun Location.toAddress(): String {
    return LOCATION_ADDRESSES.entries
        .minByOrNull { (coords, _) ->
            // Find closest match using simple distance calculation
            abs(coords.first - latitude) + abs(coords.second - longitude)
        }?.value ?: "Helsinki, Finland"
}

/**
 * Mapping of coordinates to Helsinki street addresses.
 */
private val LOCATION_ADDRESSES =
    mapOf(
        (60.169418 to 24.931618) to "Salomonkatu 15, Helsinki",
        (60.169818 to 24.932906) to "Salomonkatu 6, Helsinki",
        (60.170005 to 24.935105) to "Narinken 2, Helsinki",
        (60.169108 to 24.936210) to "Simonkatu 7, Helsinki",
        (60.168355 to 24.934869) to "Annankatu 33, Helsinki",
        (60.167560 to 24.932562) to "Fredrikinkatu 59, Helsinki",
        (60.168254 to 24.931532) to "Kampinkuja 2, Helsinki",
        (60.169012 to 24.930341) to "Fredrikinkatu 46, Helsinki",
        (60.170085 to 24.929569) to "Eteläinen Rautatiekatu 8, Helsinki",
    )
