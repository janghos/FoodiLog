package com.foodilog.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodilog.DTO.Shop
import com.foodilog.DTO.surround.SurroundParam
import com.foodilog.FoodilogApplication
import com.foodilog.PrefConstant
import com.foodilog.R
import com.foodilog.SharedPrefHandler
import com.foodilog.activity.BaseActivity
import com.foodilog.adapter.ShopAdapter
import com.foodilog.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.invoke.ConstantCallSite

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    lateinit var binding: FragmentHomeBinding
    private val pref by lazy { FoodilogApplication.prefs } // Lazy 초기화
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        observe()
        val location = pref.getFloat(PrefConstant.KEY_LAT, 0f).toString() + "," + pref.getFloat(
            PrefConstant.KEY_LONG,
            0f
        ).toString()
        val param = SurroundParam(
            location = location
        )
        (requireActivity() as BaseActivity).shopViewModel.fetchShopList(param)
        return binding.root
    }

    private fun initializer() {
    }

    private fun observe() {
        (requireActivity() as BaseActivity).shopViewModel.shopFetchResult.observe(requireActivity()) { result ->
            result.onSuccess { response ->
                val shops = mutableListOf<Shop>()

                response.results.forEach { shopResult ->

                    // 매장 이름과 주소 등 기본 정보
                    val shop = Shop(
                        imageUrl = null,
                        name = shopResult.name,
                        address = shopResult.vicinity,
                        latitude = shopResult.geometry.location.lat,
                        longitude = shopResult.geometry.location.lng
                    )

                    shops.add(shop)
                }

                binding.rvShopList.adapter = ShopAdapter(shops) // 어댑터에 리스트 전달
                binding.rvShopList.layoutManager = LinearLayoutManager(requireContext())
            }.onFailure {
                Toast.makeText(requireContext(), "네트워크 확인", Toast.LENGTH_SHORT).show()
            }
        }
    }
}