package com.foodilog.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import com.foodilog.R
import com.foodilog.databinding.FragmentAddReviewBinding

class AddReviewFragment : Fragment() {

    lateinit var binding : FragmentAddReviewBinding
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

    fun initialization(){
        binding.ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            // rating 변수에 선택된 별점 값이 저장됩니다.
            // 이 값을 사용하여 원하는 작업을 수행할 수 있습니다.
        }
    }
}