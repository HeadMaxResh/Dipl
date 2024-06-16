package com.example.dipl.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.CircleTransformation
import com.example.dipl.R
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class ChatUserAdapter(private val userList: List<User>/*, private val onItemClick: (User) -> Unit*/) :
    RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder>() {

    interface OnChatClickListener {
        fun onChatClick(user: User)
    }

    fun setOnChatClickListener(listener: ChatUserAdapter.OnChatClickListener) {
        this.onChatClickListener = listener
    }

    private var onChatClickListener: ChatUserAdapter.OnChatClickListener? = null

    inner class ChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        private val userProfileImageView: ImageView = itemView.findViewById(R.id.userProfileImageView)

        fun bind(user: User) {
            userNameTextView.text = "${user.name} ${user.surname}"
            val imagePath = user.photoUser
            imagePath?.let {
                val file = File(it)
                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .transform(CircleTransformation())
                        .into(userProfileImageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ChatUserViewHolder(view)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onChatClickListener?.onChatClick(user)
        }


    }

}