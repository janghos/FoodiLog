package com.foodilog.DTO.surround

import com.foodilog.KeyConstant

data class SurroundParam(
    val keyword : String = "맛집",
    val location: String, // 예: "37.422,-122.084"
    val radius : Int = 1500,      // 반경 (미터 단위)
    val type : String = "restaurant",
    val key : String = KeyConstant.API_KEY
)
