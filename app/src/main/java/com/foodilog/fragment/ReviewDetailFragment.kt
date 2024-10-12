package com.foodilog.fragment

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.compose.ui.semantics.text
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foodilog.databinding.FragmentReviewDetailBinding
import com.foodilog.local.FoodiDataBase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class ReviewDetailFragment : DialogFragment() {

    private var _binding: FragmentReviewDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentReviewDetailBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        dialog.setContentView(binding.root)
        val reviewId = arguments?.getInt("reviewId", 0) ?: 0

        binding.ivClose.setOnClickListener {
            dismissAllowingStateLoss()
        }

        lifecycleScope.launch {
            val review =
                FoodiDataBase.getDatabase(requireContext()).reviewDao().getReviewById(reviewId)
            Log.d("ReviewDetailFragment", "review: $review") // 로그 추가

            // 데이터 바인딩
            binding.tvShopName.text = review.shopName
            binding.tvShopAddress.text = review.shopAddress
            binding.tvReviewTitle.text = "제목 : " + review.reviewTitle
            binding.tvReviewContent.text = review.reviewContent
            binding.tvRating.text = review.rating.toString()
            binding.tvDate.text = formatDateFromLong(review.date)

            // 이미지 로드
            if (review.imagePaths.isNotEmpty()) {
                binding.imageContainer.gravity = Gravity.CENTER

                for (imagePath in review.imagePaths) {
                    val imageView = ImageView(requireContext())
                    imageView.setImageURI(Uri.parse(imagePath))

                    val layoutParams = LinearLayout.LayoutParams(350, 350)
                    layoutParams.setMargins(10, 10, 10, 10)
                    imageView.layoutParams = layoutParams

                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.imageContainer.addView(imageView)
                }

                // 이미지 개수에 따라 LinearLayout weight 설정
                if (review.imagePaths.size == 1) {
                    // 이미지가 하나일 때는 weight를 0으로 설정하여 가운데 정렬
                    (binding.imageContainer.getChildAt(0).layoutParams as LinearLayout.LayoutParams).weight =
                        0f
                } else if (review.imagePaths.size > 1) {
                    // 이미지가 여러 개일 때는 weight를 1로 설정하여 같은 비중으로 나열
                    for (i in 0 until binding.imageContainer.childCount) {
                        (binding.imageContainer.getChildAt(i).layoutParams as LinearLayout.LayoutParams).weight =
                            1f
                    }
                }
            }
            // Google Map 설정
            binding.mapView.onCreate(savedInstanceState)
            binding.mapView.onResume()
            binding.mapView.getMapAsync { googleMap ->
                val storeLocation = LatLng(review.latitude, review.longitude) // 위도, 경도 값
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 15f))
                googleMap.addMarker(MarkerOptions().position(storeLocation).title(review.shopName))
            }
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun formatDateFromLong(dateLong: Long): String {
        val dateString = dateLong.toString()
        val year = dateString.substring(0, 4)
        val month = dateString.substring(4, 6)
        val day = dateString.substring(6, 8)
        return "${year}년 ${month}월 ${day}일"
    }
}