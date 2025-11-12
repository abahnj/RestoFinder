package com.wolt.restofinder.domain.model

/**
 * Domain model representing a geographic location.
 *
 * Simple value object for coordinates used in location-based queries.
 *
 * @property latitude Latitude coordinate (-90 to 90)
 * @property longitude Longitude coordinate (-180 to 180)
 */
data class Location(
    val latitude: Double,
    val longitude: Double
)
