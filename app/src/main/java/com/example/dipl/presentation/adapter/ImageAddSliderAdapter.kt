package com.example.dipl.presentation.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView

import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.dipl.R


class ImageAddSliderAdapter(
    private val viewPager: ViewPager?,
    private val images: MutableList<ByteArray>
) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context)
            .inflate(R.layout.add_slider_item, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.iv_back)
        val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDelete)

        val imageByteArray = images[position]
        val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        imageView.setImageBitmap(bitmap)

        btnDelete.setOnClickListener {

            images.removeAt(position)
            container.removeView(itemView)
            if (images.size < 1) {
                val layoutParams = viewPager?.layoutParams
                layoutParams?.height = 0
                viewPager?.layoutParams = layoutParams
            }
            notifyDataSetChanged()

        }

        container.addView(itemView)
        return itemView
        /*val imageView = ImageView(container.context)
        val bitmap = BitmapFactory.decodeByteArray(images[position], 0, images[position].size)
        imageView.setImageBitmap(bitmap)
        container.addView(imageView)
        return imageView*/
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


    override fun getCount(): Int {
        return images.size
    }

    override fun getItemPosition(`object`: Any): Int {
        val index = images.indexOf((`object` as View).tag)
        return if (index == -1) POSITION_NONE else index
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun addImage(imageByteArray: ByteArray) {

        images.add(imageByteArray)

        val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        val imageHeight = bitmap.height

        val layoutParams = viewPager?.layoutParams
        layoutParams?.height = imageHeight
        viewPager?.layoutParams = layoutParams

        notifyDataSetChanged()
    }
}
