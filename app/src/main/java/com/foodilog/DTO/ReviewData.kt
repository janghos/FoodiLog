package com.foodilog.DTO

import android.media.Image
import androidx.compose.ui.graphics.Path
import java.util.Date

data class ReviewData(
    val date : String,
    val rating : Float ?= 0f,
    val reviewShopInfo : ShopInfoData ?= null,
    val reviewTitle : String ?= "",
    val reviewContent : String ?= "",
    val imagePath: List<String>
)
