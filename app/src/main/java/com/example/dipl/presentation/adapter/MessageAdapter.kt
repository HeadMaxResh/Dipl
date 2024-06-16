package com.example.dipl.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R
import com.example.dipl.domain.model.Chat
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.viewholder.ChatViewHolder

class MessageAdapter(private val context: Context, private val chats: List<Chat>, private val currentUser: User, private val targetUser: User) : RecyclerView.Adapter<ChatViewHolder>() {

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(context)
        return if (viewType == MSG_TYPE_RIGHT) {
            val view = inflater.inflate(R.layout.chat_item_right, parent, false)
            val chatViewHolder = ChatViewHolder(view)
            chatViewHolder.setChatItemProfileImageViewType(ChatViewHolder.currentUserType)
            chatViewHolder
        } else {
            val view = inflater.inflate(R.layout.chat_item_left, parent, false)
            val chatViewHolder = ChatViewHolder(view)
            chatViewHolder.setChatItemProfileImageViewType(ChatViewHolder.targetUserType)
            chatViewHolder
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]

        // Установка текста сообщения
        holder.getChatItemMessageTextView().text = chat.content

        // Установка профиля чата
        /*if (holder.getChatItemProfileImageViewType() == ChatViewHolder.currentUserType) {
            if (currentUser.imageURL == "null") {
                if (currentUser.gender == "Male") {
                    holder.getChatItemProfileImageView().setImageResource(R.drawable.profile_default_male)
                } else if (currentUser.gender == "Female") {
                    holder.getChatItemProfileImageView().setImageResource(R.drawable.profile_default_female)
                }
            }
        } else if (holder.getChatItemProfileImageViewType() == ChatViewHolder.targetUserType) {
            if (targetUser.imageURL == "null") {
                if (targetUser.gender == "Male") {
                    holder.getChatItemProfileImageView().setImageResource(R.drawable.profile_default_male)
                } else if (targetUser.gender == "Female") {
                    holder.getChatItemProfileImageView().setImageResource(R.drawable.profile_default_female)
                }
            }
        }*/
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chats[position].senderId.toInt() == currentUser.id) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}