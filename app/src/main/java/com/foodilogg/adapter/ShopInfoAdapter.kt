package com.foodilogg.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.layout.layout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.foodilogg.DTO.ShopInfoData
import com.foodilogg.R

class ShopInfoAdapter(private val onItemClick: (ShopInfoData) -> Unit) : RecyclerView.Adapter<ShopInfoAdapter.ViewHolder>() {

    private var shopInfoList: List<ShopInfoData> = emptyList()

    fun updateResults(results: List<ShopInfoData>) {
        shopInfoList = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shopInfo = shopInfoList[position]
        holder.bind(shopInfo)
        holder.itemView.setOnClickListener { onItemClick(shopInfo) }
    }

    override fun getItemCount(): Int = shopInfoList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvShopName: TextView = itemView.findViewById(R.id.tv_shop_name)
        private val tvShopAddress: TextView = itemView.findViewById(R.id.tv_shop_address)

        fun bind(shopInfo: ShopInfoData) {
            tvShopName.text = shopInfo.name
            tvShopAddress.text = shopInfo.address

            // Set font, text size, and max lines
            val typeface = ResourcesCompat.getFont(itemView.context, R.font.baemin)
            tvShopName.typeface = typeface
            tvShopAddress.typeface = typeface
            tvShopName.maxLines = 1
            tvShopAddress.maxLines = 1
        }
    }
}