package com.foodilog.DTO.surround


import kotlinx.serialization.SerialName

@Serializable
data class Viewport(
    @SerialName("northeast")
    val northeast: Northeast,
    @SerialName("southwest")
    val southwest: Southwest
)