package com.wolt.restofinder.data.mapper

import com.wolt.restofinder.data.remote.dto.ImageDto
import com.wolt.restofinder.data.remote.dto.RestaurantItemDto
import com.wolt.restofinder.data.remote.dto.VenueDetailsDto
import com.wolt.restofinder.domain.model.Venue
import org.junit.Assert.assertEquals
import org.junit.Test

class VenueMapperTest {

    @Test
    fun `toDomain maps all fields correctly`() {
        val dto = RestaurantItemDto(
            image = ImageDto(
                url = "https://example.com/image.jpg",
                blurhash = "LEHV6nWB2yk8"
            ),
            venue = VenueDetailsDto(
                id = "venue123",
                name = "Test Restaurant",
                shortDescription = "Great food"
            )
        )

        val expected = Venue(
            id = "venue123",
            name = "Test Restaurant",
            description = "Great food",
            blurHash = "LEHV6nWB2yk8",
            imageUrl = "https://example.com/image.jpg",
            isFavourite = false
        )

        assertEquals(expected, dto.toDomain())
    }

    @Test
    fun `toDomain handles null shortDescription`() {
        val dto = RestaurantItemDto(
            image = ImageDto(url = "https://example.com/image.jpg", blurhash = "hash"),
            venue = VenueDetailsDto(id = "venue123", name = "Test", shortDescription = null)
        )

        val result = dto.toDomain()
        assertEquals(null, result.description)
    }

    @Test
    fun `toDomain applies isFavourite flag`() {
        val dto = RestaurantItemDto(
            image = ImageDto(url = "https://example.com/image.jpg", blurhash = "hash"),
            venue = VenueDetailsDto(id = "venue123", name = "Test", shortDescription = null)
        )

        val favourite = dto.toDomain(isFavourite = true)
        assertEquals(true, favourite.isFavourite)

        val notFavourite = dto.toDomain(isFavourite = false)
        assertEquals(false, notFavourite.isFavourite)
    }
}
