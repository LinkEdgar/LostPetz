package com.example.enduser.lostpetz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_pet_search_detail.*
import android.location.Geocoder
import android.util.Log
import com.bumptech.glide.request.RequestOptions
import java.io.IOException


class PetSearchDetailActivity : AppCompatActivity() {

    var pet: Pet ?= null
    private val GOOGLE_STATIC_MAP_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?"
    private val GOOGLE_API_KEY = "AIzaSyD5fotiQ4E6IDK56KG5LGwtrkew8v_VIvI"
    private val GOOGLE_STATIC_MAP_ZOOM = "&zoom=12"
    private var Latitude: Double? = null
    private var Longitude: Double ?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_search_detail)

        handleIntentData()
        setPetData()
        getStaticMapParamaters()
        setStaticMap()
    }

    private fun handleIntentData(){
        pet = intent.getParcelableExtra("pet")
    }

    /*
    Sets views data based on passed in pet object
     */
    private fun setPetData(){
        Glide.with(this).load(pet!!.profileUrl).into(search_detail_imageview)
        search_detail_description_textview.setText(pet?.description)
        search_detail_name_textview.setText(pet?.name)
    }

    private fun setStaticMap(){
        val staticMapUrl = GOOGLE_STATIC_MAP_BASE_URL + "center=" + Latitude + "," + Longitude + GOOGLE_STATIC_MAP_ZOOM + "&size=600x300&maptype=rpadmap&key=" + GOOGLE_API_KEY
        Glide.with(this).applyDefaultRequestOptions(RequestOptions().centerCrop()).load(staticMapUrl).into(search_detail_static_map)
    }
    
    private fun getStaticMapParamaters() {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocationName(pet!!.zip, 1)
            if (addresses != null && !addresses.isEmpty()) {
                val address = addresses[0]
                Latitude = address.latitude
                Longitude = address.longitude
                val city = address.locality
                Log.e("City ", "--> $city")
                search_detail_city_textview.text = city
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
