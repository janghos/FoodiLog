package com.foodilog.DTO.surround

data class SurroundParam(
    val keyword : String ?= null,
    val location : String ?= null,
    val radius : String ?= null,
    val type : String = "restaurant",
    val key : String = "AIzaSyANHqu3Cn0-HlzbO3H1sy-SomZcokuU810"
)
