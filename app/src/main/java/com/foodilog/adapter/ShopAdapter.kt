package com.foodilog.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foodilog.DTO.Shop
import com.foodilog.databinding.SurroundShopListBinding

class ShopAdapter(private val shopList: List<Shop>) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    inner class ShopViewHolder(private val binding: SurroundShopListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(shop: Shop) {
            shop.imageUrl?.let {
                binding.ivShopImage.setImageURI(Uri.parse(it))
            }
            binding.tvShopName.text = shop.name
            binding.tvShopAddress.text = shop.address

            val latitude = shop.latitude
            val longitude = shop.longitude

            // 네이버 지도 URI
            val naverMapUri = Uri.parse("nmap://place?lat=$latitude&lng=$longitude&name=${shop.name}")
            // 구글 지도 명시적으로 호출
            val googleMapUri = Uri.parse("http://maps.google.com/maps?q=loc:$latitude,$longitude")

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
        holder.bind(shopList[position])
    }

    override fun getItemCount(): Int = shopList.size
}