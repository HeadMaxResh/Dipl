package com.example.dipl.presentation.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R

class UserListViewHolder(val view: View) : RecyclerView.ViewHolder(view)  {
    val usernameTextView: TextView = view.findViewById(R.id.userNameTextView)
    val userImageView: ImageView = view.findViewById(R.id.userProfileImageView)

}