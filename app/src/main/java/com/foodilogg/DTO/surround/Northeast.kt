package com.foodilogg.DTO.surround


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Northeast(
    @SerialName("lat")
    val lat: Double,
    @SerialName("lng")
    val lng: Double
)