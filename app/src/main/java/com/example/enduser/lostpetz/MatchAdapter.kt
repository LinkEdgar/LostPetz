package com.example.enduser.lostpetz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.match_container.view.*

open class MatchAdapter(val listOfPotentialMatches: ArrayList<MatchInfo>,val context: Context,val onClick: onClicked ) : BaseAdapter(){

    interface onClicked{
        fun nextClicked()
        fun prevClicked()
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

        val profileImage = view!!.findViewById<View>(R.id.match_container_imageview) as ImageView

        Glide.with(context).applyDefaultRequestOptions(RequestOptions().centerCrop()).load(listOfPotentialMatches[position].url).into(profileImage)

        /*
        val nextImageButton = view!!.findViewById<View>(R.id.match_container_next)

        nextImageButton.setOnClickListener({onNextClick()})

        val detailImageButton = view!!.findViewById<View>(R.id.match_container_detail)

        detailImageButton.setOnClickListener({onDetailClick()})

        val bookmarkImageButton = view!!.findViewById<View>(R.id.match_container_bookmark)

        bookmarkImageButton.setOnClickListener({onBookmarkClick()})
        */

        //TODO delete this later
        view.test_textview.setText(listOfPotentialMatches[position].name)

        return view!!
    }

    private fun onNextClick(){
        val toast = Toast.makeText(context, "Next Clicked", Toast.LENGTH_SHORT)
        toast.show()
        onClick!!.nextClicked()
    }

    private fun onDetailClick(){
        onClick?.prevClicked()
    }

    private fun onBookmarkClick(){
        onClick?.bookmarkClicked()
    }
}