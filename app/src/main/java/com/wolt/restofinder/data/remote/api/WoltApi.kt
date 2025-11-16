package com.wolt.restofinder.data.remote.api

import com.wolt.restofinder.data.remote.dto.WoltApiResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WoltApi {
    @GET("v1/pages/restaurants")
    suspend fun getRestaurants(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): WoltApiResponseDto
}
