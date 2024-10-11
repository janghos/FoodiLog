package com.foodilog.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodilog.DTO.ShopInfoData
import com.foodilog.DTO.surround.SearchSurroundAreaData
import com.foodilog.KeyConstant
import com.foodilog.R
import com.foodilog.adapter.ShopInfoAdapter
import com.foodilog.databinding.FragmentSearchDialogBinding
import com.foodilog.shop.ShopViewModel

class SearchDialogFragment : DialogFragment() {

    lateinit var binding : FragmentSearchDialogBinding
    private val shopViewModel: ShopViewModel by activityViewModels() // 수정된 부분
    private lateinit var shopInfoAdapter: ShopInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSearchDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        observe()
        initialize()

        dialog?.let{safeDialog->
            safeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            safeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        return binding.root
    }

    private fun initialize(){

        binding.etSearchShop.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val searchQuery = s.toString()
                if (searchQuery.isNotEmpty()) {
                    shopViewModel.searchPlaces(searchQuery, KeyConstant.API_KEY)
                } else {
                    // Clear search results
                }
            }

        })

        binding.ivClose.setOnClickListener {
            dismiss()
        }

    }
    private fun observe(){
        shopViewModel.searchResults.observe(viewLifecycleOwner) { result ->
           result.onSuccess { response ->

               val shopInfoList = mutableListOf<ShopInfoData>()
               response.results.forEach {
                   val shopInfoData = ShopInfoData(
                       name = it.name,
                       address = it.formatted_address,
                       placeId = it.place_id,
                       latitude = it.geometry.location.lat,
                       longitude = it.geometry.location.lng
                   )
                   shopInfoList.add(shopInfoData)
               }
               shopInfoAdapter = ShopInfoAdapter { selectedPlaceResult ->
                   // Create ShopInfoData and pass it to AddReviewFragment
                   val shopInfoData = ShopInfoData(
                       name = selectedPlaceResult.name,
                       address = selectedPlaceResult.address,
                       placeId = selectedPlaceResult.placeId,
                       latitude = selectedPlaceResult.latitude,
                       longitude = selectedPlaceResult.longitude
                   )
                   // Pass shopInfoData to AddReviewFragment using a callback or shared ViewModel
                   (parentFragment as? AddReviewFragment)?.onShopInfoSelected(shopInfoData)
                   dismiss()
               }
               shopInfoAdapter.updateResults(shopInfoList)
               binding.rvSearchShop.adapter = shopInfoAdapter
               binding.rvSearchShop.layoutManager = LinearLayoutManager(requireContext())

           }.onFailure {

           }
        }
    }
}