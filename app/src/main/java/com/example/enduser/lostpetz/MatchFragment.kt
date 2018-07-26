package com.example.enduser.lostpetz

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.daprlabs.aaron.swipedeck.SwipeDeck
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_match.view.*

/*
    This fragment allows the user to look through a variety of pets to potentially "match" with one. The user can skip to the next pet, favorite a pet,
    or look at detailed information of the current pet
 */

open class MatchFragment: Fragment(), MatchAdapter.onClicked{

    private var cardSwipe: SwipeDeck? = null
    private var matchAdapter: MatchAdapter ?= null
    private var dataSet: ArrayList<MatchInfo> ?= null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val MY_PERMISSIONS_REQUEST_LOCATION = 3141


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_match, container, false)
        //TODO delete fake data
        dataSet = ArrayList<MatchInfo>()
        dataSet?.add(MatchInfo("1","1"))
        dataSet?.add(MatchInfo("2","2"))
        dataSet?.add(MatchInfo("3","3"))
        dataSet?.add(MatchInfo("4","4"))
        initCardSwipe(rootView)
        getUserLocation()

        return rootView
    }

    private fun initCardSwipe(rootView: View){
        cardSwipe = rootView.swipe_deck
        matchAdapter = MatchAdapter(dataSet!!,context!!, this)
        cardSwipe?.setAdapter(matchAdapter)

        cardSwipe?.setCallback(object : SwipeDeck.SwipeDeckCallback {
            override fun cardSwipedLeft(stableId: Long) {
                //TODO implement left swipe logic
                val toast = Toast.makeText(context, "Card swiped left", Toast.LENGTH_SHORT)
                toast.show()
            }

            override fun cardSwipedRight(stableId: Long) {
                //TODO implement right swipe logic
                val toast = Toast.makeText(context, "Card swiped right", Toast.LENGTH_SHORT)
                toast.show()
            }

        })
    }

    override fun prevClicked() {
        //TODO start intent to switch into detail activity
        val toast = Toast.makeText(context, "Detail Button clicked", Toast.LENGTH_SHORT)
        toast.show()
        cardSwipe?.unSwipeCard()
    }

    override fun nextClicked() {
        cardSwipe!!.swipeTopCardLeft(180)
        //TODO figure out delete options for objects in dataset

    }

    override fun bookmarkClicked() {
        //TETST CODE TODO delete
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
        val toast = Toast.makeText(context, "Bookmark clicked", Toast.LENGTH_SHORT)
        toast.show()
        //TODO call firebase to set bookmark and maybe toast that it's complet
    }

    private fun loadCards(){
        val zipCode = "12345"
        //TODO investigate best way to calculate distance
        //geo fencing might be the best choice as far as I can tell
    }

    private fun getUserLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION)
        }else{
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location: Location? ->
                extractZipCode(location) }
        }
    }

    private fun extractZipCode(location: Location?){
        val latitude = location?.latitude
        val longitude = location?.longitude
        val gcd = Geocoder(context)
        val address: List<Address> = gcd!!.getFromLocation(latitude!!, longitude!!,10)
        Log.e("address", "--> $address")

        for(x in address){
            if(x.locality != null && x.postalCode != null){
                Log.e("state", "--> ${x.adminArea}")
                Log.e("locality", "${x.locality}")
                Log.e("zip", "--> ${x.postalCode}")
            }
        }
    }

    //TODO in background load cities within distance
    private fun loadCities(){

    }
}