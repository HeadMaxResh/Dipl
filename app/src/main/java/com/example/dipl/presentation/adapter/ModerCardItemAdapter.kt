package com.example.dipl.presentation.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.dipl.R
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.fragment.ModerApartmentListFragmentDirections
import java.io.File

class ModerCardItemAdapter(
    private val apartmentInfos: List<ApartmentInfo>,
    private val user: User?
) : RecyclerView.Adapter<ModerCardItemAdapter.ModerCardItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(apartmentInfo: ApartmentInfo)
        fun onRemove(apartmentInfo: ApartmentInfo)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    inner class ModerCardItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val viewPager: ViewPager = itemView.findViewById(R.id.vp_detail)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvCity: TextView = view.findViewById(R.id.tv_city)
        val tvRent: TextView = view.findViewById(R.id.tv_rent)
        val tvRate: TextView = view.findViewById(R.id.tv_rate)
        val btnDel: Button = view.findViewById(R.id.del)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModerCardItemViewHolder {
        val layout = R.layout.item_card_moder
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ModerCardItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apartmentInfos.size
    }

    override fun onBindViewHolder(holder: ModerCardItemViewHolder, position: Int) {
        val currentItem = apartmentInfos[position]
        val images =
            currentItem.listImages.map { imagePath -> getImageByteArrayFromFile(imagePath) }
                .toMutableList()

        val adapter = ImageSliderAdapter(images)
        holder.viewPager.adapter = adapter
        holder.tvName.text = currentItem.name.toString()
        holder.tvCity.text = currentItem.city.toString()
        holder.tvRent.text = currentItem.rent.toString()
        holder.tvRate.text = currentItem.rate.toString()

        val isFavorite = currentItem.isFavorite

        if (currentItem.hide) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.view.context, R.color.light_gray))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.view.context, android.R.color.white))
        }

        //setHeartButton(holder, isFavorite)
        //buttonOnClickListener(holder, currentItem, isFavorite)

        holder.btnDel.setOnClickListener {
            val action = ModerApartmentListFragmentDirections.actionModerApartmentListFragmentToDialogFragment(currentItem.id)
            holder.itemView.findNavController().navigate(action)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(apartmentInfos[position])
        }
    }


    private fun getImageByteArrayFromFile(imagePath: String): ByteArray {
        val file = File(imagePath)
        return file.readBytes()
    }
}
