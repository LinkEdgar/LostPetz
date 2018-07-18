package com.example.enduser.lostpetz

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.image_slider_container.view.*

open class ImageSlider(val context: Context, val urlList: ArrayList<String>, val click: onClick) : PagerAdapter(){

    /*
    interface to handle creating a full screen dialog fragment
     */
    interface onClick{
        fun onClicked(position:Int)
    }

    val inflater = LayoutInflater.from(context)
    /*
    Even if the pet has no images we must populate the imageview with a default imageview
     */
    override fun getCount(): Int {
        return if(urlList.size > 0){
            urlList.size
        }else 1
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    //inflates view
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = inflater.inflate(R.layout.image_slider_container, container, false)
        //TODO add no profile picture drawable
        if(urlList.size > 0) { //if the pet has pictures to upload we use our urlList to populate the imageview
            Glide.with(context).applyDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher)).load(urlList[position]).into(itemView.image_slider_imageview)
        }else{ //if there are no images we populate the imageview with default image
            Glide.with(context).load(R.mipmap.ic_launcher).into(itemView.image_slider_imageview)
        }
        //full screen image call back via our interface
        itemView.setOnClickListener{click.onClicked(position)}
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }
}