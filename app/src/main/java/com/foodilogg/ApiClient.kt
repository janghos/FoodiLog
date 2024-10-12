package com.foodilogg

import com.foodilogg.DTO.surround.SearchSurroundAreaData
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {
    @GET("place/nearbysearch/json")
    suspend fun searchNearbyRestaurant(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String,
        @Query("language") language : String = "ko"
    ): SearchSurroundAreaData

    @GET("place/textsearch/json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("key") apiKey: String,
        @Query("type") type: String = "restaurant",
        @Query("language") language : String = "ko"
    ): SearchSurroundAreaData
}