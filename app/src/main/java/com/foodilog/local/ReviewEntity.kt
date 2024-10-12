package com.foodilog.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "shop_name") val shopName: String?,
    @ColumnInfo(name = "shop_address") val shopAddress: String?,
    @ColumnInfo(name = "review_title") val reviewTitle: String,
    @ColumnInfo(name = "review_content") val reviewContent: String,
    @ColumnInfo(name = "rating") val rating: Float,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "image_paths") val imagePaths: List<String?>,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReviewEntity

        if (id != other.id) return false
        if (shopName != other.shopName) return false
        if (shopAddress != other.shopAddress) return false
        if (reviewTitle != other.reviewTitle) return false
        if (reviewContent != other.reviewContent) return false
        if (rating != other.rating) return false
        if (date != other.date) return false
        if (imagePaths != other.imagePaths) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (shopName?.hashCode() ?: 0)
        result = 31 * result + (shopAddress?.hashCode() ?: 0)
        result = 31 * result + reviewTitle.hashCode()
        result = 31 * result + reviewContent.hashCode()
        result = 31 * result + rating.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + imagePaths.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }
}