package com.example.dipl.presentation.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        const val currentUserType = 0
        const val targetUserType = 1
    }

    private var chatItemMessageTextView: TextView
    //private var chatItemProfileImageView: ImageView
    private var chatItemProfileImageViewType: Int = 0

    init {
        chatItemMessageTextView = itemView.findViewById(R.id.chatItemMessageTextView)
        //chatItemProfileImageView = itemView.findViewById(R.id.chatItemProfileImageView)
    }

    fun getChatItemMessageTextView(): TextView {
        return chatItemMessageTextView
    }

    /*fun getChatItemProfileImageView(): ImageView {
        return chatItemProfileImageView
    }*/

    fun getChatItemProfileImageViewType(): Int {
        return chatItemProfileImageViewType
    }

    fun setChatItemProfileImageViewType(chatItemProfileImageViewType: Int) {
        this.chatItemProfileImageViewType = chatItemProfileImageViewType
    }
}