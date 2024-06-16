package com.example.dipl.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diplback.diplserver.model.Review
import com.example.dipl.R
import java.io.File

class ReviewSliderAdapter(private val reviews: MutableList<Review>): RecyclerView.Adapter<ReviewSliderAdapter.ReviewSliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewSliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.review_min_card, parent, false
        )
        return ReviewSliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewSliderViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    inner class ReviewSliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivProfile: ImageView = itemView.findViewById(R.id.iv_profile)
        private val tvRate: TextView = itemView.findViewById(R.id.tv_rate)
        private val tvOwnerName: TextView = itemView.findViewById(R.id.tv_owner_name)
        private val tvCommentText: TextView = itemView.findViewById(R.id.comment_text)
        private val tvFlawsText: TextView = itemView.findViewById(R.id.flaws_text)
        private val tvDignityText: TextView = itemView.findViewById(R.id.dignity_text)


        fun bind(review: Review) {

            tvRate.text = review.rate.toString()
            tvOwnerName.text = review.user.name + " " + review.user.surname
            tvDignityText.text = review.dignityText
            tvFlawsText.text = review.flawsText
            tvCommentText.text = review.commentText


        }
    }
}