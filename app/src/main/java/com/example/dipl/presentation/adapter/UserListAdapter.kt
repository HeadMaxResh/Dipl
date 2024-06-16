package com.example.dipl.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R
import com.example.dipl.domain.model.User

class UserListAdapter(
    private val userList: List<User>,
    private val userCurrent: User
): RecyclerView.Adapter<UserListAdapter.UserListViewHolder>()  {

    inner class UserListViewHolder(val view: View) : RecyclerView.ViewHolder(view)  {
        val usernameTextView: TextView = view.findViewById(R.id.userNameTextView)
        val userImageView: ImageView = view.findViewById(R.id.userProfileImageView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val layout = R.layout.user_item
        val view = LayoutInflater.from(parent.context).inflate(layout, parent,false)
        return UserListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.userImageView.setImageResource(R.drawable.ic_profile)
        holder.usernameTextView.text = currentUser.name + " " + currentUser.surname
    }
}