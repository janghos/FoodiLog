package com.foodilog.shop

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodilog.DTO.surround.SearchSurroundAreaData
import com.foodilog.DTO.surround.SurroundParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    // 상점 목록 가져오기 결과를 나타내는 LiveData
    val shopFetchResult: MutableLiveData<Result<SearchSurroundAreaData>> = MutableLiveData()

    // 상점 목록 가져오기 함수
    fun fetchShopList(param: SurroundParam) {
        viewModelScope.launch {
            val result = shopRepository.getShopList(param)
            shopFetchResult.postValue(result)
        }
    }
}
