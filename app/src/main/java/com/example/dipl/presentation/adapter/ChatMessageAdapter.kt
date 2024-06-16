package com.example.dipl.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.CircleTransformation
import com.example.dipl.R
import com.example.dipl.domain.model.Message
import com.example.dipl.domain.model.User
import com.squareup.picasso.Picasso
import java.io.File

class ChatMessageAdapter(var messages: List<Message>, private val currentUser: User) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val MESSAGE_TYPE_SENT = 1
    private val MESSAGE_TYPE_RECEIVED = 2

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.chatItemMessageTextView)
        //val profileImageView: ImageView = itemView.findViewById(R.id.chatItemProfileImageView)
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.chatItemMessageTextView)
        val profileImageView: ImageView = itemView.findViewById(R.id.chatItemProfileImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MESSAGE_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.messageTextView.text = message.content

        } else if (holder is ReceivedMessageViewHolder) {
            holder.messageTextView.text = message.content
            val imageByteArray = currentUser.photoUser // Assuming you have a field to store the image path in the User class
            imageByteArray?.let {
                val file = File(it)
                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .transform(CircleTransformation())
                        .into(holder.profileImageView)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUser.id) {
            MESSAGE_TYPE_RECEIVED
        } else {
            MESSAGE_TYPE_SENT
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}