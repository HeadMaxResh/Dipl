package com.example.dipl.presentation.adapter

import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter

class ImageSliderAdapter(
    private val images: MutableList<ByteArray>
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val imageView = ImageView(container.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        val bitmap = BitmapFactory.decodeByteArray(
            images[position],
            0,
            images[position].size
        )

        imageView.setImageBitmap(bitmap)

        container.addView(imageView)

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as ImageView)
    }

    override fun getCount() = images.size

    override fun isViewFromObject(view: View, obj: Any) = view === obj
}