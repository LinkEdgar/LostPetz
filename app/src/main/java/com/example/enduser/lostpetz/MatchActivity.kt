package com.example.enduser.lostpetz

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.daprlabs.aaron.swipedeck.SwipeDeck
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_match.*
import kotlinx.android.synthetic.main.fragment_match.view.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.math.absoluteValue

open class MatchActivity: AppCompatActivity(), SwipeDeck.SwipeDeckCallback{

    private var cardSwipe: SwipeDeck? = null
    private var matchAdapter: MatchAdapter ?= null
    private var dataSet: ArrayList<MatchInfo> ?= null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val MY_PERMISSIONS_REQUEST_LOCATION = 3141
    private var cityHashSet: HashSet<String> ?= null
    private var rootView: View?= null
    private val MATCH_LIST_KEY = "match_key"
    private val MATCH_HASHSET_KEY = "hash_set"

    private var userZipCode: String ?= null

    //firebase
    private var mMatchRef: DatabaseReference?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUi()

        if(savedInstanceState == null) {
            cityHashSet = HashSet()
            dataSet = ArrayList()
            //fakePopulateHashset()
            getUserLocation()
        }
    }

    private fun initUi(){
        match_back_button.setOnClickListener{backClicked()}
        match_next_button.setOnClickListener{nextClicked()}
        match_fav_button.setOnClickListener{favClicked()}
    }

    /*
    Sets up the cardswipe and its adapters as well as swipe related callbacks
     */
    private fun initCardSwipe(){
        cardSwipe = swipe_deck
        matchAdapter = MatchAdapter(dataSet!!,this)
        cardSwipe?.setAdapter(matchAdapter)

        cardSwipe?.setCallback(this)
    }

    /*
    Interface for back button clicked
     */
    private fun backClicked() {
        //TODO start intent to switch into detail activity
        val toast = Toast.makeText(this, "Detail Button clicked", Toast.LENGTH_SHORT)
        toast.show()
        cardSwipe?.unSwipeCard()
    }

    /*
    Interface for next button clicked
     */
    private fun nextClicked() {
        cardSwipe!!.swipeTopCardLeft(180)
        //TODO figure out delete options for objects in dataset

    }

    /*
    Interface for favorites button clicked
     */
    private fun favClicked() {
        //TETST CODE TODO delete
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
        val toast = Toast.makeText(this, "Bookmark clicked", Toast.LENGTH_SHORT)
        toast.show()
        //TODO call firebase to set bookmark and maybe toast that it's complet
    }

    private fun loadCards(){
        val zipCode = "12345"
        //TODO investigate best way to calculate distance
        //geo fencing might be the best choice as far as I can tell
    }

    /*
    Tries to access user location if the app has permission otherwise it asks for permission
     */
    private fun getUserLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION)
        }else{
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location: Location? ->
                extractZipCode(location)
                initFirebase() }
        }
    }

    /*
    Gets user location and passes zip code to loadCities()
     */
    private fun extractZipCode(location: Location?){
        val latitude = location?.latitude
        val longitude = location?.longitude
        val gcd = Geocoder(this)
        val address: List<Address> = gcd!!.getFromLocation(latitude!!, longitude!!,10)
        //Log.e("address", "--> $address")

        for(x in address){
            if(x.locality != null && x.postalCode != null){
                // Log.e("state", "--> ${x.adminArea}")
                //Log.e("locality", "${x.locality}")
                //Log.e("zip", "--> ${x.postalCode}")
                userZipCode = x.postalCode
            }
        }
        //loadCities(zipCode)
    }

    private fun initFirebase(){
        mMatchRef = FirebaseDatabase.getInstance().getReference("Match")
        mMatchRef!!.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                loadMatches(p0!!)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }

        })
    }

    /*
    Using a distance calculation api and Okhttpclient we load up all zip codes that are within a 'X' amount
    distance,in miles, from the user's current location zip code. The zip code is added to a hashset to filter
    pets
     */
    private fun loadCities(zipCode: String?){
        //TODO remove key from this method
        //TODO add pased in zip to url
        val applicationKey = "OV3Ep9sFnGnSLc30fL1icVQlCet4T24kVBtH0yNIgguq6ZntbaJccNsV1CVVqS2U"
        val base = "https://www.zipcodeapi.com/rest/"
        val detail = "/radius.json/30168/10/mile"
        val client = OkHttpClient()
        val request = Request.Builder().url(base+applicationKey+detail).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body()!!.string()
                    val json = JSONObject(responseData)
                    val jsonArray = json.getJSONArray("zip_codes")
                    // Log.e("jsonarray", "$jsonArray")
                    for(x in 0..jsonArray.length()){ //iterates through the json array and extracts all unique zip codes
                        val jObject = jsonArray.getJSONObject(x)
                        val zipCode = jObject.getString("zip_code")
                        cityHashSet!!.add(zipCode!!)
                    }
                } catch (e: JSONException) {

                }

            }
        })

    }

    private fun fakePopulateHashset(){
        cityHashSet!!.add("70801")
        cityHashSet!!.add("70805")
        cityHashSet!!.add("70809")
        cityHashSet!!.add("70813")
        cityHashSet!!.add("70819")


    }
    /*
    Takes in the datasnapshot and filters out pets via the hashset full of valid zip codes
    after all the pets are loaded the cardSwipeInit() method is called
     */

    /*
    TODO delete comment block
    private fun loadMatches(dataSnapshot: DataSnapshot){
        for(x in dataSnapshot.children){
            val zip = x.child("zipCode").getValue(String::class.java)
            if(cityHashSet!!.contains(zip)){
                val name = x.child("name").getValue(String::class.java)
                val pictureUrl = x.child("pictureUrl").getValue(String::class.java)
                val match = MatchInfo(name!!, pictureUrl!!)
                dataSet?.add(match)
            }
        }
        initCardSwipe()
    }
    */

    //Cheap algorithm
    //TODO make sure the first digit is a match
    private fun loadMatches(dataSnapshot: DataSnapshot){
        for(x in dataSnapshot.children){
            val zip = x.child("zipCode").getValue(String::class.java)
            //TODO zip may be null?
            if(zip != null) {
                val secondDigit = zip!!.elementAt(2)?.toInt().minus(userZipCode?.elementAt(2)!!.toInt())?.absoluteValue
                val thirdDigit = zip?.elementAt(3)?.toInt()?.minus(userZipCode?.elementAt(3)!!.toInt())?.absoluteValue
                Log.e("secondDigit", "--> $thirdDigit")

                if (secondDigit!! <= 2 && thirdDigit!! <= 5) {
                    val name = x.child("name").getValue(String::class.java)
                    val pictureUrl = x.child("pictureUrl").getValue(String::class.java)
                    val match = MatchInfo(name!!, pictureUrl!!)
                    dataSet?.add(match)
                }
            }
        }
        initCardSwipe()
    }


    override fun cardSwipedLeft(stableId: Long) {
        //TODO implement left swipe logic
        val toast = Toast.makeText(this, "Card swiped left", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun cardSwipedRight(stableId: Long) {
        //TODO implement right swipe logic
        val toast = Toast.makeText(this, "Card swiped right", Toast.LENGTH_SHORT)
        toast.show()
    }

    //Saves hashset and match data
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(MATCH_LIST_KEY, dataSet)
        outState.putSerializable(MATCH_HASHSET_KEY, cityHashSet)
    }

    /*
    Restores the match dataset as well as the hashset with all of the filtered cities
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState != null) {
            if (savedInstanceState!!.containsKey(MATCH_LIST_KEY)) {
                dataSet = savedInstanceState?.getSerializable(MATCH_LIST_KEY) as ArrayList<MatchInfo>
                cardSwipe = rootView!!.swipe_deck
                matchAdapter = MatchAdapter(dataSet!!,this)
                cardSwipe?.setAdapter(matchAdapter)

                cardSwipe?.setCallback(this)
            }
            if(savedInstanceState.containsKey(MATCH_HASHSET_KEY)){
                cityHashSet = savedInstanceState.get(MATCH_HASHSET_KEY) as HashSet<String>
            }
        }
    }
}