package com.foodilog.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodilog.DTO.ShopInfoData
import com.foodilog.DTO.surround.SurroundParam
import com.foodilog.FoodilogApplication
import com.foodilog.PrefConstant
import com.foodilog.R
import com.foodilog.activity.BaseActivity
import com.foodilog.adapter.RecentReviewsAdapter
import com.foodilog.adapter.ShopAdapter
import com.foodilog.databinding.FragmentHomeBinding
import com.foodilog.local.FoodiDataBase
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    lateinit var binding: FragmentHomeBinding
    private val pref by lazy { FoodilogApplication.prefs }
    private val placesClient by lazy {FoodilogApplication.placesClient}
    private lateinit var recentReviewsAdapter: RecentReviewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
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

        binding.btnAdd.setOnClickListener {
            (requireActivity() as BaseActivity).replaceFragment(AddReviewFragment())
        }

        recentReviewsAdapter = RecentReviewsAdapter(requireContext())
        recentReviewsAdapter.setOnItemClickListener(object : RecentReviewsAdapter.OnItemClickListener {
            override fun onItemClick(reviewId: Int) {
                val fragment = ReviewDetailFragment()
                val args = Bundle()
                args.putInt("reviewId", reviewId)
                fragment.arguments = args
                fragment.show(childFragmentManager, "ReviewDetailFragment")
            }
        })

        binding.viewpagerRecentReviews.adapter = recentReviewsAdapter
        TabLayoutMediator(binding.tabLayoutRecentReviews, binding.viewpagerRecentReviews) { tab, position ->
            // tab.text = "Tab ${position + 1}" // 필요에 따라 탭 텍스트 설정
        }.attach()

        lifecycleScope.launch {
            val recentReviews = FoodiDataBase.getDatabase(requireContext()).reviewDao().getRecentReviews(5)
            recentReviewsAdapter.submitList(recentReviews)

            // 최근 리뷰가 없을 경우 안내 메시지 표시
            if (recentReviews.isEmpty()) {
                // 안내 메시지 표시
                binding.tabLayoutRecentReviews.visibility = View.GONE
                binding.viewpagerRecentReviews.visibility = View.GONE
                binding.tvReview.text = "최근 등록된 리뷰가 없습니다."
            } else {
                binding.tabLayoutRecentReviews.visibility = View.VISIBLE
                binding.viewpagerRecentReviews.visibility = View.VISIBLE
                binding.tvReview.text = "최근 등록한 리뷰"
            }
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

                    val shopInfoData = ShopInfoData(
                        placeId = shopResult.place_id,
                        name = shopResult.name,
                        address = shopResult.vicinity,
                        latitude = shopResult.geometry.location.lat,
                        longitude = shopResult.geometry.location.lng,
                        bitmap = null
                    )

                    // 이미지 로드
                    val placeId = shopResult.place_id
                    val fields = listOf(Place.Field.PHOTO_METADATAS)
                    val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)

                    placesClient.fetchPlace(placeRequest)
                        .addOnSuccessListener { placeResponse: FetchPlaceResponse ->
                            val place = placeResponse.place
                            val photoMetadatas = place.photoMetadatas
                            if (photoMetadatas != null && photoMetadatas.isNotEmpty()) {
                                val photoMetadata = photoMetadatas.first()
                                val photoRequest =
                                    FetchPhotoRequest.builder(photoMetadata)
                                    .build()

                                placesClient.fetchPhoto(photoRequest)
                                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                                        shopInfoData.bitmap = fetchPhotoResponse.bitmap // bitmap 필드에 저장
                                        binding.rvShopList.adapter?.notifyItemChanged(shopInfoDataList.indexOf(shopInfoData)) // 어댑터에 변경 사항 알림
                                    }
                                // ... (onFailure 처리) ...
                            } else {
                                // 사진 메타데이터가 없으면 기본 이미지를 설정하거나 표시하지 않습니다.
                                shopInfoData.bitmap = BitmapFactory.decodeResource(resources, R.drawable.no_image) // 기본 이미지 설정
                                binding.rvShopList.adapter?.notifyItemChanged(shopInfoDataList.indexOf(shopInfoData)) // 어댑터에 변경 사항 알림
                            }
                        }
                    // 매장 이름과 주소 등 기본 정보

                    shopInfoDataList.add(shopInfoData)
                }
                binding.rvShopList.itemAnimator = null
                binding.rvShopList.setItemViewCacheSize(Integer.MAX_VALUE) // 캐시 크기를 최대로 설정
                binding.rvShopList.adapter = ShopAdapter(shopInfoDataList, placesClient)
                binding.rvShopList.layoutManager = LinearLayoutManager(requireContext())

            }.onFailure {
                Toast.makeText(requireContext(), "네트워크 확인", Toast.LENGTH_SHORT).show()
            }
        }
    }
}