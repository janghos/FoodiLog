package com.foodilog.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodilog.DTO.ShopInfoData
import com.foodilog.DTO.surround.SurroundParam
import com.foodilog.FoodilogApplication
import com.foodilog.PrefConstant
import com.foodilog.R
import com.foodilog.activity.BaseActivity
import com.foodilog.adapter.ShopAdapter
import com.foodilog.databinding.FragmentHomeBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    lateinit var placesClient: PlacesClient
    lateinit var binding: FragmentHomeBinding
    private val pref by lazy { FoodilogApplication.prefs }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        Places.initialize(requireContext(), "키대체")
        // Create PlacesClient
        placesClient = Places.createClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initialization()
        observe()
        callShopList()
        return binding.root
    }

    private fun initialization(){
        val radius = requireActivity().applicationContext.resources.getDimension(R.dimen.corner_radius)
        binding.ivRecent.shapeAppearanceModel = binding.ivRecent.shapeAppearanceModel
            .toBuilder()
            .setAllCornerSizes(radius)
            .build()

        binding.btnAdd.setOnClickListener {
            (requireActivity() as BaseActivity).replaceFragment(AddReviewFragment())
        }
    }
    private fun callShopList(){
        val location =
            pref.getFloat(PrefConstant.KEY_LAT, 0f).toString() +
                    "," +
                    pref.getFloat(PrefConstant.KEY_LONG, 0f).toString()
        val param = SurroundParam(
            location = location
        )
        (requireActivity() as BaseActivity).shopViewModel.fetchShopList(param)
    }

    private fun observe() {
        (requireActivity() as BaseActivity).shopViewModel.shopFetchResult.observe(requireActivity()) { result ->
            result.onSuccess { response ->
                val shopInfoDataList = mutableListOf<ShopInfoData>()

                response.results.forEach { shopResult ->
                    // 매장 이름과 주소 등 기본 정보
                    val shopInfoData = ShopInfoData(
                        placeId = shopResult.place_id,
                        name = shopResult.name,
                        address = shopResult.vicinity,
                        latitude = shopResult.geometry.location.lat,
                        longitude = shopResult.geometry.location.lng
                    )

                    shopInfoDataList.add(shopInfoData)
                }

                binding.rvShopList.adapter = ShopAdapter(shopInfoDataList, placesClient)
                binding.rvShopList.layoutManager = LinearLayoutManager(requireContext())

            }.onFailure {
                Toast.makeText(requireContext(), "네트워크 확인", Toast.LENGTH_SHORT).show()
            }
        }
    }
}