package com.example.dipl.presentation.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R

class CardItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val tvArea: TextView = view.findViewById(R.id.tv_name)
    val tvStreet: TextView = view.findViewById(R.id.tv_city)
    val tvPrice: TextView = view.findViewById(R.id.tv_rent)
    val tvRate: TextView = view.findViewById(R.id.tv_rate)
}