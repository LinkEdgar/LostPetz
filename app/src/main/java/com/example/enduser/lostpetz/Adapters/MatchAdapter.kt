package com.example.enduser.lostpetz.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.enduser.lostpetz.CustomObjectClasses.MatchInfo
import com.example.enduser.lostpetz.R
import kotlinx.android.synthetic.main.match_container.view.*

open class MatchAdapter(val listOfPotentialMatches: ArrayList<MatchInfo>, val context: Context) : BaseAdapter(){


    val layoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return listOfPotentialMatches!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listOfPotentialMatches!![position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView
        if(view == null) {
            view = layoutInflater!!.inflate(R.layout.match_container, parent, false)
        }

        val profileImage = view!!.findViewById<View>(R.id.match_container_imageview) as ImageView

        Glide.with(context).applyDefaultRequestOptions(RequestOptions().centerCrop()).load(listOfPotentialMatches[position].url).into(profileImage)


        //TODO delete this later
        view.test_textview.setText(listOfPotentialMatches[position].name)

        return view!!
    }
}