package com.foodilogg.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodilogg.adapter.ReviewListAdapter
import com.foodilogg.databinding.FragmentHistoryBinding
import com.foodilogg.local.FoodiDataBase
import kotlinx.coroutines.launch

class HistoryFragment : BaseFragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val reviewList = FoodiDataBase.getDatabase(requireContext()).reviewDao().getAllReviews()
            val adapter = ReviewListAdapter(requireContext(), reviewList) { review ->
                val reviewDetailFragment = ReviewDetailFragment()
                val bundle = Bundle()
                bundle.putInt("reviewId", review.id)
                reviewDetailFragment.arguments = bundle
                reviewDetailFragment.show(childFragmentManager, "reviewDetail")
            }

            binding.rvHistory.adapter = adapter
            binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}