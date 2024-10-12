package com.foodilog.DTO

import android.media.Image
import java.util.Date

data class ReviewData(
    val date : Date,
    val rating : String ?= "",
    val reviewShopInfo : ShopInfoData ?= null,
    val reviewTitle : String ?= "",
    val reviewContent : String ?= "",
    val reviewImage : List<Image> ?= null
)
