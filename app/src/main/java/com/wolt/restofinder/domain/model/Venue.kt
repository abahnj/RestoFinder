package com.wolt.restofinder.domain.model

/**
 * Domain model representing a restaurant/venue.
 *
 * This is a pure domain model with no Android or framework dependencies.
 * Used throughout the app's business logic layer.
 *
 * @property id Unique identifier for the venue
 * @property name Display name of the venue
 * @property description Short description of the venue (nullable)
 * @property imageUrl URL for the venue's image
 * @property isFavourite Whether the user has marked this venue as favourite
 *
 * Note: No isFromCache field - Wolt team confirmed we show error when offline,
 * not cached data. User is moving between locations, old data is not relevant.
 */
data class Venue(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String,
    val isFavourite: Boolean = false
)
