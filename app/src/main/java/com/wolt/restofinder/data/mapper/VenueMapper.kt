package com.wolt.restofinder.data.mapper

import com.wolt.restofinder.data.remote.dto.RestaurantItemDto
import com.wolt.restofinder.domain.model.Venue

fun RestaurantItemDto.toDomain(isFavourite: Boolean = false): Venue {
    requireNotNull(venue) { "RestaurantItemDto must have venue to map to domain" }
    return Venue(
        id = venue.id,
        name = venue.name,
        description = venue.shortDescription,
        blurHash = image.blurhash,
        imageUrl = image.url,
        isFavourite = isFavourite
    )
}
