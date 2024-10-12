package com.foodilogg.DTO

import com.google.gson.annotations.Expose
import java.io.Serializable

data class DateDTO (
    @Expose
    var year: Int = 0,
    @Expose
    var month: Int = 0,
    @Expose
    var day: Int = 0,
) : Serializable, Cloneable {

}