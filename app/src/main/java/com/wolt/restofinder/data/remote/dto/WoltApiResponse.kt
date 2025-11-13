package com.wolt.restofinder.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ASSUMPTION: API always returns sections in same order:
 * - sections[0]: Categories (we ignore)
 * - sections[1]: Restaurants with venue details (we use this)
 */
@Serializable
data class WoltApiResponseDto(
    val sections: List<SectionDto>
)

@Serializable
data class SectionDto(
    val items: List<RestaurantItemDto>
)

@Serializable
data class RestaurantItemDto(
    val image: ImageDto,
    val venue: VenueDetailsDto
)

@Serializable
data class VenueDetailsDto(
    val id: String,
    val name: String,
    @SerialName("short_description")
    val shortDescription: String? = null
)

@Serializable
data class ImageDto(
    val url: String,
    val blurhash: String
)
