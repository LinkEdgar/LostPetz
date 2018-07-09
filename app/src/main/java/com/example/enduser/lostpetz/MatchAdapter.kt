package com.example.enduser.lostpetz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast

open class MatchAdapter(list: ArrayList<MatchInfo>, context: Context, onClick: onClicked ) : BaseAdapter(){

    interface onClicked{
        fun nextClicked()
        fun backClicked()
    }

    var onClick: onClicked ? = onClick
    val layoutInflater = LayoutInflater.from(context)
    var listOfPotentialMatches: ArrayList<MatchInfo>? = list
    var context: Context?= context

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
        val imageView = view!!.findViewById<View>(R.id.nextImageView)

        imageView.setOnClickListener({onNextClick()})

        return view!!
    }

    open fun onNextClick(){
        val toast = Toast.makeText(context, "Next Clicked", Toast.LENGTH_SHORT)
        toast.show()
        onClick!!.nextClicked()
    }

    open fun onBackClick(){
        onClick?.backClicked()
    }

}