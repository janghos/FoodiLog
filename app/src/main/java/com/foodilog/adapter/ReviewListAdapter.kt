package com.foodilog.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodilog.R
import com.foodilog.local.ReviewEntity
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.format

class ReviewListAdapter(private val context: Context, private val reviewList: List<ReviewEntity>, private val itemClickListener: (ReviewEntity) -> Unit) :
    RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.tv_date)
        val star: TextView = itemView.findViewById(R.id.rating)
        val shopImage: ImageView = itemView.findViewById(R.id.iv_shop_image)
        val shopName: TextView = itemView.findViewById(R.id.tv_shop_name)
        val reviewTitle: TextView = itemView.findViewById(R.id.tv_review_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviewList[position]

        // 대표 이미지 설정 (첫 번째 이미지 사용)
        if (review.imagePaths.isNotEmpty()) {
            val imageUri = Uri.parse(review.imagePaths[0])
            Glide.with(context).load(imageUri).into(holder.shopImage)
        } else {
            // 기본 이미지 설정
            holder.shopImage.setImageResource(R.drawable.no_image) // 기본 이미지 리소스
        }

        holder.star.text = review.rating.toString()
        holder.date.text = formatDateFromLong(review.date)
        holder.reviewTitle.text = review.reviewTitle
        holder.shopName.text = review.shopName

        holder.itemView.setOnClickListener {
            itemClickListener(review)
        }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
    fun formatDateFromLong(dateLong: Long): String {
        val dateString = dateLong.toString()
        val year = dateString.substring(0, 4)
        val month = dateString.substring(4, 6)
        val day = dateString.substring(6, 8)
        return "${year}년 ${month}월 ${day}일"
    }
}