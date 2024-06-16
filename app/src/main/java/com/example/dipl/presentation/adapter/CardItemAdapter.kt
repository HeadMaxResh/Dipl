package com.example.dipl.presentation.adapter

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.dipl.R
import com.example.dipl.data.api.Api.userApiService
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CardItemAdapter(
    private val apartmentInfos: List<ApartmentInfo>,
    private val user: User?
) : RecyclerView.Adapter<CardItemAdapter.CardItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(apartmentInfo: ApartmentInfo)
        fun onAddToFavorites(apartmentInfo: ApartmentInfo)
        fun onRemoveFromFavorites(apartmentInfo: ApartmentInfo)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    inner class CardItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val viewPager: ViewPager = itemView.findViewById(R.id.vp_detail)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvCity: TextView = view.findViewById(R.id.tv_city)
        val tvRent: TextView = view.findViewById(R.id.tv_rent)
        val tvRate: TextView = view.findViewById(R.id.tv_rate)
        val btnHeart: Button = view.findViewById(R.id.heart)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val layout = R.layout.item_card
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return CardItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apartmentInfos.size
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
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

        setHeartButton(holder, isFavorite)
        buttonOnClickListener(holder, currentItem, isFavorite)

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(apartmentInfos[position])
        }
    }

    private fun buttonOnClickListener(
        holder: CardItemViewHolder,
        currentItem: ApartmentInfo,
        isFavorite: Boolean
    ) {
        holder.btnHeart.setOnClickListener {
            if (currentItem.userOwner.id == user?.id) {
                Toast.makeText(holder.itemView.context, "Вы не можете сохранять в понравившиеся собственные квартиры", Toast.LENGTH_SHORT).show()
            } else {
                if (isFavorite) {
                    holder.btnHeart.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.light_gray
                        )
                    )
                    onItemClickListener?.onRemoveFromFavorites(currentItem)
                } else {
                    holder.btnHeart.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.red
                        )
                    )
                    onItemClickListener?.onAddToFavorites(currentItem)
                }
            }
        }
    }

    private fun setHeartButton(holder: CardItemViewHolder, isFavorite: Boolean) {
        if (isFavorite) {
            holder.btnHeart.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.red))
        } else {
            holder.btnHeart.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.light_gray
                )
            )
        }
    }

    private fun getImageByteArrayFromFile(imagePath: String): ByteArray {
        val file = File(imagePath)
        return file.readBytes()
    }
}

/*
class CardItemAdapter(
    private val apartmentInfos: List<ApartmentInfo>
) : RecyclerView.Adapter<CardItemAdapter.CardItemViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(apartmentInfo: ApartmentInfo)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }


    inner class CardItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        //var imageViewList: List<Int> = view.findViewById(R.id.viewPager)
        val viewPager: ViewPager = itemView.findViewById(R.id.viewPager)
        val tvArea: TextView = view.findViewById(R.id.tv_area)
        //val tvStreet: TextView = view.findViewById(R.id.tv_street)
        val tvRent: TextView = view.findViewById(R.id.tv_rent)
        val tvRate: TextView = view.findViewById(R.id.tv_rate)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val layout = R.layout.item_card
        val view = LayoutInflater.from(parent.context).inflate(layout, parent,false)
        return CardItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apartmentInfos.size
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {

        val currentItem = apartmentInfos[position]
        val images = currentItem.listImages
        // Привязка данных из CardItem к представлениям
        //holder.imageViewList = currentItem.listImages
        val adapter = ImageSliderAdapter(images)
        holder.viewPager.adapter = adapter
        holder.tvArea.text = currentItem.area.toString()
        //holder.tvStreet.text = currentItem.street
        holder.tvRent.text = currentItem.rent.toString()
        holder.tvRate.text = currentItem.rate.toString()

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(apartmentInfos[position])
        }

        */
/*holder.itemView.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(apartmentInfo)
            navController.navigate(action)
        }*//*

    }
}*/
