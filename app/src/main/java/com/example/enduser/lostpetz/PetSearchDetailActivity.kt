package com.example.enduser.lostpetz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pet_search_detail.*
class PetSearchDetailActivity : AppCompatActivity() {

    var pet: Pet ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_search_detail)

        handleIntentData()
        setPetData()
    }

    fun handleIntentData(){
        pet = intent.getParcelableExtra("pet")
    }

    /*
    Sets views data based on passed in pet object
     */
    fun setPetData(){
        search_detail_description_textview.setText(pet?.description)
        search_detail_name_textview.setText(pet?.name)
    }
}
