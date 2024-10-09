package com.foodilog.DTO.surround


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    @SerialName("business_status")
    val businessStatus: String,
    @SerialName("geometry")
    val geometry: Geometry,
    @SerialName("icon")
    val icon: String,
    @SerialName("icon_background_color")
    val iconBackgroundColor: String,
    @SerialName("icon_mask_base_uri")
    val iconMaskBaseUri: String,
    @SerialName("name")
    val name: String,
    @SerialName("opening_hours")
    val openingHours: OpeningHours?,
    @SerialName("photos")
    val photos: List<Photo>?,
    @SerialName("place_id")
    val placeId: String,
    @SerialName("plus_code")
    val plusCode: PlusCode?,
    @SerialName("price_level")
    val priceLevel: Int?,
    @SerialName("rating")
    val rating: Double?,
    @SerialName("reference")
    val reference: String,
    @SerialName("scope")
    val scope: String,
    @SerialName("types")
    val types: List<String>,
    @SerialName("user_ratings_total")
    val userRatingsTotal: Int?,
    @SerialName("vicinity")
    val vicinity: String
)
