package com.foodilog.DTO

import android.graphics.Bitmap

data class ShopInfoData(
    val placeId : String?= "",
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    var bitmap : Bitmap ?= null
)