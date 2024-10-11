package com.foodilog.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodilog.databinding.FragmentHistoryBinding

class HistoryFragment : BaseFragment() {

    lateinit var binding : FragmentHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

}