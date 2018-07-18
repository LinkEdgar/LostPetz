package com.example.enduser.lostpetz

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.image_slider_container.view.*

open class ImageSlider(val context: Context, val urlList: ArrayList<String>) : PagerAdapter(){

    val inflater = LayoutInflater.from(context)
    override fun getCount(): Int {
        return urlList.size
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = inflater.inflate(R.layout.image_slider_container, container, false)
        //TODO add no profile picture drawable
        Glide.with(context).applyDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher)).load(urlList[position]).into(itemView.image_slider_imageview)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }
}