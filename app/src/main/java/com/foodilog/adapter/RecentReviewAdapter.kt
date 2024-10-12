package com.foodilog.adapter

import android.net.Uri
import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodilog.R
import com.foodilog.databinding.ItemRecentReviewBinding
import com.foodilog.local.ReviewEntity

class RecentReviewsAdapter(private val context: android.content.Context) : ListAdapter<ReviewEntity, RecentReviewsAdapter.ViewHolder>(DIFF_CALLBACK) {

    // 아이템 클릭 리스너 인터페이스
    interface OnItemClickListener {
        fun onItemClick(reviewId: Int)
    }

    // 아이템 클릭 리스너
    private var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(private val binding: ItemRecentReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ReviewEntity) {
            val radius = context.resources.getDimension(R.dimen.corner_radius)
            binding.ivShopImage.shapeAppearanceModel = binding.ivShopImage.shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(radius)
                .build()

            binding.tvTitle.text = review.reviewTitle
            binding.ratingBar.rating = review.rating

            if (review.imagePaths.isNotEmpty()) {
                val imageUri = Uri.parse(review.imagePaths[0])
                Glide.with(context).load(imageUri).into(binding.ivShopImage)
            }
            itemView.setOnClickListener {
                onItemClickListener?.let {
                    it.onItemClick(review.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    companion object {
        private val DIFF_CALLBACK = object : androidx.recyclerview.widget.DiffUtil.ItemCallback<ReviewEntity>() {
            override fun areItemsTheSame(oldItem: ReviewEntity, newItem: ReviewEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ReviewEntity, newItem: ReviewEntity): Boolean {
                return oldItem.equals(newItem)
            }
        }
    }
}