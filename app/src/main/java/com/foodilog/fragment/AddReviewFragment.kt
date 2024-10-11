package com.foodilog.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.RelativeLayout
import com.foodilog.DeviceUtil
import com.foodilog.FoodilogApplication
import com.foodilog.HeightProvider
import com.foodilog.R
import com.foodilog.activity.MainActivity
import com.foodilog.databinding.FragmentAddReviewBinding

class AddReviewFragment : Fragment() {

    lateinit var binding : FragmentAddReviewBinding
    private val placesClient by lazy { FoodilogApplication.placesClient}
    private var heightProvider : HeightProvider ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddReviewBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initialization()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        heightProvider?.let {
            it.dismiss()
        }
    }

    fun initialization() {
        binding.ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->

            }


        //키보드 bottom navigation 숨김처리
        heightProvider = HeightProvider(requireActivity()).init().setHeightListener { height ->
            val layoutParams = binding.rlLayout.layoutParams as FrameLayout.LayoutParams
            if (height == 0) {
                // 키보드가 내려갔을 때
                (requireActivity() as MainActivity).visibleNav()
                layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT // 원래 높이로 복원
            } else {
                // 키보드가 올라왔을 때
                (requireActivity() as MainActivity).goneNav()
                layoutParams.height = binding.rlLayout.height - height // 높이를 줄임
            }
            binding.rlLayout.layoutParams = layoutParams
        }

        //지도 검색
    }
}