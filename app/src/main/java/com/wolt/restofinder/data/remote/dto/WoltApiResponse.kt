package com.wolt.restofinder.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WoltApiResponseDto(
    val sections: List<SectionDto>
)

@Serializable
data class SectionDto(
    val name: String,
    val items: List<RestaurantItemDto>
)

@Serializable
data class RestaurantItemDto(
    val image: ImageDto,
    val venue: VenueDetailsDto? = null
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
