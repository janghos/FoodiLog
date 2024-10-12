package com.foodilog.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReviewDao {
    @Insert
    suspend fun insertReview(review: ReviewEntity)

    @Query("SELECT * FROM reviews")
    suspend fun getAllReviews(): List<ReviewEntity>

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: Int): ReviewEntity
}