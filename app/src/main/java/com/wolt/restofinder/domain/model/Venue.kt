package com.wolt.restofinder.domain.model

data class Venue(
    val id: String,
    val name: String,
    val description: String?,
    val blurHash: String,
    val imageUrl: String,
    val isFavourite: Boolean = false
)
