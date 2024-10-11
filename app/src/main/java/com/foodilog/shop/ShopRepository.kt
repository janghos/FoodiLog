package com.foodilog.shop

import com.foodilog.ApiClient
import com.foodilog.DTO.surround.SearchSurroundAreaData
import com.foodilog.DTO.surround.SurroundParam
import java.lang.Exception
import javax.inject.Inject

class ShopRepository @Inject constructor(
    private val apiClient: ApiClient
){
    suspend fun getShopList(param: SurroundParam): Result<SearchSurroundAreaData> {
        return try {
            // API 호출 시 필요한 쿼리 파라미터를 전달
            val response = apiClient.searchNearbyRestaurant(
                location = param.location,
                radius = param.radius,
                type = param.type,
                apiKey = param.key
            )
            Result.success(response) // 성공적으로 응답을 받아옴
        } catch (e: Exception) {
            Result.failure(e) // 에러 발생 시
        }
    }

    suspend fun searchPlaces(query: String, apiKey: String): Result<SearchSurroundAreaData> {
        return try {
            val response = apiClient.searchPlaces(query, apiKey)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}