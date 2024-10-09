package com.foodilog.DTO.surround


import kotlinx.serialization.SerialName

@Serializable
data class Geometry(
    @SerialName("location")
    val location: Location,
    @SerialName("viewport")
    val viewport: Viewport
)