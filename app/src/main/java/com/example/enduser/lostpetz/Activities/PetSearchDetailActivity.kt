package com.example.enduser.lostpetz.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_pet_search_detail.*
import android.location.Geocoder
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.example.enduser.lostpetz.FullScreenDialog
import com.example.enduser.lostpetz.ImageSlider
import com.example.enduser.lostpetz.CustomObjectClasses.Pet
import com.example.enduser.lostpetz.R
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException


class PetSearchDetailActivity : AppCompatActivity(), ImageSlider.onClick {

    var pet: Pet?= null
    private val GOOGLE_STATIC_MAP_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?"
    private val GOOGLE_API_KEY = "AIzaSyANQ3_5ZqmUERNIkc2rTZaBcyVzBipYEXo"
    private val GOOGLE_STATIC_MAP_ZOOM = "&zoom=12"
    private var Latitude: Double? = null
    private var Longitude: Double ?= null
    private var urlList: ArrayList<String> ?= null
    private val POSTER_ID_KEY = "posterId"

    private var mAuth: FirebaseAuth ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_search_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mAuth = FirebaseAuth.getInstance()
        urlList = ArrayList()
        handleIntentData()
        setPetData()
        getStaticMapParameters()
        setStaticMap()
        setUpImageSlider()

        search_detail_lost_button.setOnClickListener{lostFoundButton()}
    }
    
    private fun handleIntentData(){
        pet = intent.getParcelableExtra("pet")
        if(pet!!.isFoundPet)
            date_lost_label.setText(R.string.date_found_status_label)
        else
            date_lost_label.setText(R.string.date_lost_status_label)
    }

    /*
    Sets views data based on passed in pet object
     */
    private fun setPetData(){
        search_detail_description_textview.setText(pet?.description)
        search_detail_name_textview.setText(pet?.name)
        if(pet?.dateLost != null)
        search_detail_date_lost.text = pet?.dateLost
        else search_detail_date_lost.text = "N/A"
        if(pet?.breed != null && !pet?.breed.equals("Null"))
            search_detail_breed.text = pet?.breed
        else search_detail_breed.text = "N/A"

    }

    /*
    sets static map via glide
     */
    private fun setStaticMap(){
        val staticMapUrl = GOOGLE_STATIC_MAP_BASE_URL + "center=" + Latitude + "," + Longitude + GOOGLE_STATIC_MAP_ZOOM + "&size=600x300&maptype=rpadmap&key=" + GOOGLE_API_KEY
        Glide.with(this).applyDefaultRequestOptions(RequestOptions().centerCrop()).load(staticMapUrl).into(search_detail_static_map)
    }

    /*
    Using the zip code the user has provided for us we extract coordinates to display a static map image of the general area
    where the pet was lost. We also populate a view based on the city extracted from the zip code
     */
    private fun getStaticMapParameters() {
        val geoCoder = Geocoder(this)
        try {
            val addresses = geoCoder.getFromLocationName(pet!!.zip, 1)
            if (addresses != null && !addresses.isEmpty()) {
                val address = addresses[0]
                Latitude = address.latitude
                Longitude = address.longitude
                val city = address.locality
                if(city != null && !city.equals("Null")) search_detail_city_textview.text = city
                else search_detail_city_textview.text = "N/A"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /*
    This methods gets a count of how many pictures if any the pet has and adds them to
    the urlList array if they are not null.
    An ImageSlider() adapter is created with the list of urls for the pet images
    A circular indicator is attached to our viewpager
     */

    private fun setUpImageSlider(){
        if(pet!!.profileUrlOne != null && !pet!!.profileUrlOne.equals("null")){
            urlList!!.add(pet!!.profileUrlOne)
        }
        if(pet!!.profileUrlTwo != null && !pet!!.profileUrlTwo.equals("null") ){
            urlList!!.add(pet!!.profileUrlTwo)
        }
        if(pet!!.profileUrlThree != null && !pet!!.profileUrlThree.equals("null")){
            urlList!!.add(pet!!.profileUrlThree)
        }
        val adapter = ImageSlider(this, urlList!!, this)
        search_detail_viewpager.adapter = adapter
        search_detail_indicator.setViewPager(search_detail_viewpager)
    }

    /*
    This interface creates a full screen dialog of the selected image
     */
    override fun onClicked(position: Int) {
        if(urlList!!.size >0) {
            val fullScreenDialog = FullScreenDialog()
            fullScreenDialog.setImageUrl(urlList!![position])
            fullScreenDialog.show(supportFragmentManager, "fullscreen")
        }
    }

    /*
    This method will switch the user to a messaging activity if they are not the poster of the pet.
    One cannot chat with themselves about their lost pet
     */
    private fun lostFoundButton(){
        if(mAuth!!.currentUser!!.uid != pet!!.userID ) {
            val switchToMessageIntent = Intent(this, MessagingActivity::class.java)
            switchToMessageIntent.putExtra(POSTER_ID_KEY, pet!!.userID)
            startActivity(switchToMessageIntent)
        }else Toast.makeText(this, R.string.poster_error, Toast.LENGTH_SHORT).show()
    }

}
