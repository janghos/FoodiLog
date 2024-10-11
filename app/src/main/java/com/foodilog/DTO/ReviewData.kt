package com.foodilog.DTO

import android.media.Image
import java.util.Date

data class ReviewData(
    val rating : String ?= "",
    val shopName : String ?= "",
    val shopAddress : String ?= "",
    val reviewTitle : String ?= "",
    val date : Date,
    val reviewContent : String ?= "",
    val reviewImage : List<Image> ?= null
)
