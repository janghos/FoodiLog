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
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "image_paths") val imagePaths: List<String?>,
    @ColumnInfo(name = "latitude") val latitude: Double, // 위도 추가
    @ColumnInfo(name = "longitude") val longitude: Double // 경도 추가
)