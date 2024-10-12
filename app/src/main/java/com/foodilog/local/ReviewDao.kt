package com.foodilog.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReviewDao {
    @Insert
    suspend fun insertReview(review: ReviewEntity)

    @Query("SELECT * FROM reviews ORDER BY date DESC")
    suspend fun getAllReviews(): List<ReviewEntity>

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: Int): ReviewEntity

    @Query("SELECT * FROM reviews ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentReviews(limit: Int): List<ReviewEntity>
}