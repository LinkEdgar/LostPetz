package com.example.enduser.lostpetz.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.enduser.lostpetz.CustomObjectClasses.Pet
import com.example.enduser.lostpetz.R

class EditPetAdapter(var data: ArrayList<Pet>, var context: Context): RecyclerView.Adapter<EditPetAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.edit_pet_container, parent, false))
        return viewHolder
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}