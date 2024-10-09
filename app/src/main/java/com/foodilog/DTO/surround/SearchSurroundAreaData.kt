package com.foodilog.DTO.surround


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchSurroundAreaData(
    @SerialName("html_attributions")
    val htmlAttributions: List<String>,
    @SerialName("results")
    val results: List<Result>,
    @SerialName("status")
    val status: String
)