package com.example.enduser.lostpetz.Adapters

import android.content.Context
import android.net.Uri
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.enduser.lostpetz.CustomObjectClasses.Pet
import com.example.enduser.lostpetz.R

class EditPetAdapter(var data: ArrayList<Pet>, var context: Context, var mInterface: PetAdapterInterface): RecyclerView.Adapter<EditPetAdapter.ViewHolder>() {

    interface PetAdapterInterface{
        fun onDeleteClicked()
        fun onSavedButtonClicked()
        fun onPetImageSelectionSelected(imagePosition:Int, holder:ViewHolder, petPosition: Int)
        fun onCancelImageSelected(urlPosition: Int, position: Int, holder:ViewHolder)
        fun onSubmitPet()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pet = data[position]

        holder.name.setText(pet.name)
        holder.breed.setText(pet.breed)
        holder.description.setText(pet.description)
        holder.zip.setText(pet.zip)
        Log.e("Profiler Url", "--> ${pet.profileUrlOne}  ${pet.profileUrlTwo}  ${pet.profileUrlThree}"  )
        if(pet.profileUrlOne != null) // loads first pet picture if it's not null
            Glide.with(holder.itemView).applyDefaultRequestOptions(RequestOptions())
                    .load(pet.profileUrlOne)
                    .into(holder.imageOne)
        else Glide.with(holder.itemView).applyDefaultRequestOptions(RequestOptions())
                    .load(R.drawable.ic_gallery)
                    .into(holder.imageOne)

        if(pet.profileUrlTwo != null)
            Glide.with(holder.imageTwo).applyDefaultRequestOptions(RequestOptions())
                    .load(pet!!.profileUrlTwo).into(holder.imageTwo)
        else Glide.with(holder.imageTwo).load(R.drawable.ic_gallery).into(holder.imageTwo)

        if(pet.profileUrlThree != null)
            Glide.with(holder.itemView).applyDefaultRequestOptions(RequestOptions())
                    .load(pet.profileUrlThree)
                    .into(holder.imageThree)
        else Glide.with(holder.imageTwo).load(R.drawable.ic_gallery).into(holder.imageThree)

        holder.imageCancelOne.setOnClickListener{mInterface.onCancelImageSelected(0, position,holder)}
        holder.imageCancelTwo.setOnClickListener{mInterface.onCancelImageSelected(1,position,holder)}
        holder.imageCancelThree.setOnClickListener{mInterface.onCancelImageSelected(2,position,holder)}
        holder.imageOne.setOnClickListener{mInterface.onPetImageSelectionSelected(0, holder, position)}
        holder.imageTwo.setOnClickListener{mInterface.onPetImageSelectionSelected(1,holder,position)}
        holder.imageThree.setOnClickListener{mInterface.onPetImageSelectionSelected(2,holder,position)}
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.edit_pet_container, parent, false))
        return viewHolder
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_pet_button)
        val imageOne: ImageView = itemView.findViewById(R.id.pet_image_one)
        val imageTwo: ImageView = itemView.findViewById(R.id.pet_image_two)
        val imageThree: ImageView = itemView.findViewById(R.id.pet_image_three)
        val imageCancelOne: ImageView = itemView.findViewById(R.id.cancel_image_one)
        val imageCancelTwo: ImageView = itemView.findViewById(R.id.cancel_image_two)
        val imageCancelThree: ImageView = itemView.findViewById(R.id.cancel_image_three)
        val name: TextInputEditText= itemView.findViewById(R.id.name)
        val zip: TextInputEditText = itemView.findViewById(R.id.zip_code)
        val description: TextInputEditText = itemView.findViewById(R.id.description)
        val breed: TextInputEditText = itemView.findViewById(R.id.breed)
        val submitButton: FloatingActionButton = itemView.findViewById(R.id.submit_button)
    }
}