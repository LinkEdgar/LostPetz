package com.example.enduser.lostpetz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.match_container.view.*

open class MatchAdapter(val listOfPotentialMatches: ArrayList<MatchInfo>,val context: Context,val onClick: onClicked ) : BaseAdapter(){

    interface onClicked{
        fun nextClicked(position: Int)
        fun detailClicked(position: Int)
        fun bookmarkClicked()
    }

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
        val nextImageButton = view!!.findViewById<View>(R.id.match_container_next)

        nextImageButton.setOnClickListener({onNextClick(position)})

        val detailImageButton = view!!.findViewById<View>(R.id.match_container_detail)

        detailImageButton.setOnClickListener({onDetailClick(position)})

        val bookmarkImageButton = view!!.findViewById<View>(R.id.match_container_bookmark)

        bookmarkImageButton.setOnClickListener({onBookmarkClick()})

        //TODO delete this later
        view.test_textview.setText(listOfPotentialMatches[position].name)

        return view!!
    }

    fun onNextClick(position: Int){
        val toast = Toast.makeText(context, "Next Clicked", Toast.LENGTH_SHORT)
        toast.show()
        onClick!!.nextClicked(position)
    }

    fun onDetailClick(position: Int){
        onClick?.detailClicked(position)
    }

    fun onBookmarkClick(){
        onClick?.bookmarkClicked()
    }


}