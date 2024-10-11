package com.foodilog.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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

    inner class ShopViewHolder(private val binding: SurroundShopListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(shopInfoData: ShopInfoData) {

            val radius = itemView.context.resources.getDimension(R.dimen.corner_radius)
            binding.ivShopImage.shapeAppearanceModel = binding.ivShopImage.shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(radius)
                .build()

            val placeId = shopInfoData.placeId
            val fields = listOf(Place.Field.PHOTO_METADATAS)


            val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)

            placesClient.fetchPlace(placeRequest)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place

                    // Get the photo metadata.
                    val metada = place.photoMetadatas
                    if (metada == null || metada.isEmpty()) {
                        return@addOnSuccessListener
                    }
                    val photoMetadata = metada.first()

                    val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .build()
                    placesClient.fetchPhoto(photoRequest)
                        .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->

                            binding.ivShopImage.tag = shopInfoData.placeId

                            val bitmap = fetchPhotoResponse.bitmap
                            if (binding.ivShopImage.tag == shopInfoData.placeId) {
                                binding.ivShopImage.setImageBitmap(bitmap)
                            }

                        }.addOnFailureListener { exception: Exception ->
                            if (exception is ApiException) {
                                Toast.makeText(FoodilogApplication().baseContext.applicationContext, "네트워크 오류입니다." , Toast.LENGTH_SHORT).show()
                            }
                        }
                }

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
}