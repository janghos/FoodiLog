package com.foodilog.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.foodilog.DTO.ShopInfoData
import com.foodilog.FoodilogApplication
import com.foodilog.R
import com.foodilog.databinding.SurroundShopListBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class ShopAdapter(private val shopInfoDataList: List<ShopInfoData>, placesClient: PlacesClient) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    val placesClient = placesClient
    init {
        setHasStableIds(true)
    }

    inner class ShopViewHolder(private val binding: SurroundShopListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(shopInfoData: ShopInfoData) {

            val radius = itemView.context.resources.getDimension(R.dimen.corner_radius)
            binding.ivShopImage.shapeAppearanceModel = binding.ivShopImage.shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(radius)
                .build()

            binding.ivShopImage.setImageBitmap(shopInfoData.bitmap) // bitmap 필드 사용

            binding.tvShopName.text = shopInfoData.name
            binding.tvShopAddress.text = shopInfoData.address

            val latitude = shopInfoData.latitude
            val longitude = shopInfoData.longitude

            // 네이버 지도 URI
            val naverMapUri = Uri.parse("nmap://place?lat=$latitude&lng=$longitude&name=${shopInfoData.name}")
            // 구글 지도 명시적으로 호출
            val googleMapUri = Uri.parse("geo:0,0?q=${shopInfoData.name},${shopInfoData.address}")

            binding.ivMap.setOnClickListener {
                // 우선 네이버 지도 앱이 설치되어 있는지 확인
                val naverMapIntent = Intent(Intent.ACTION_VIEW, naverMapUri)
                naverMapIntent.setPackage("com.nhn.android.nmap")

                // 네이버 지도 앱이 설치되어 있지 않으면 구글 맵으로 연결
                if (naverMapIntent.resolveActivity(itemView.context.packageManager) != null) {
                    itemView.context.startActivity(naverMapIntent)
                } else {
                    // 구글 맵으로 이동 (카카오맵 제외)
                    val googleMapIntent = Intent(Intent.ACTION_VIEW, googleMapUri)
                    googleMapIntent.setPackage("com.google.android.apps.maps")
                    itemView.context.startActivity(googleMapIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SurroundShopListBinding.inflate(inflater, parent, false)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(shopInfoDataList[position])
    }

    override fun getItemCount(): Int = shopInfoDataList.size

    override fun getItemId(position: Int): Long {
        return shopInfoDataList[position].placeId.hashCode().toLong()
    }
}