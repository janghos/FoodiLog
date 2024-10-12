package com.foodilogg.DTO

data class ReviewData(
    val date : String,
    val rating : Float ?= 0f,
    val reviewShopInfo : ShopInfoData ?= null,
    val reviewTitle : String ?= "",
    val reviewContent : String ?= "",
    val imagePath: List<String>
)
