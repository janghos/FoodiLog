package com.foodilog

import com.foodilog.DTO.surround.SearchSurroundAreaData
import com.foodilog.DTO.surround.SurroundParam
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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