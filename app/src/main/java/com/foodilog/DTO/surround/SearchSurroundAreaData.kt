package com.foodilog.DTO.surround


import kotlinx.serialization.SerialName

@Serializable
data class SearchSurroundAreaData(
    @SerialName("html_attributions")
    val htmlAttributions: List<Any>,
    @SerialName("results")
    val results: List<Result>,
    @SerialName("status")
    val status: String
)