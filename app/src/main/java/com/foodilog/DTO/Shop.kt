package com.foodilog.DTO

data class Shop(
    val placeId : String?= "",
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
)