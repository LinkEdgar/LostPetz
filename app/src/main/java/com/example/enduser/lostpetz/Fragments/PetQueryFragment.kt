package com.example.enduser.lostpetz.Fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.enduser.lostpetz.Activities.PetSearchDetailActivity
import com.example.enduser.lostpetz.Adapters.PetAdapter
import com.example.enduser.lostpetz.FirebaseValues
import com.example.enduser.lostpetz.CustomObjectClasses.Pet
import com.example.enduser.lostpetz.R

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_pet_query.view.*

import java.util.ArrayList
import java.util.HashSet


/*
    This activity does allows the user to search through lost pets with filters. The two key filters being name and breed have been set to lower
    case for search optimization but are formatted when they are put in the PetAdapter. These values cannot be null as they will cause a null pointer
    exception.
 */

class PetQueryFragment : Fragment(), PetAdapter.onViewClicked{
    //Firebase
    private var mDatabase: FirebaseDatabase? = null
    private var mRef: DatabaseReference? = null
    private var mListener: ChildEventListener? = null

    //Recycler view
    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mPetAdapter: PetAdapter? = null
    private var mPetArrrayList: ArrayList<Pet>? = null

    //Views
    private var mNoPetsFoundTextView: TextView? = null
    private var mNoPetsProgressBar: ProgressBar? = null
    private var mSearchView: SearchView? = null
    //private var mNameFilterButton: Button? = null
    //private var mZipFilterButton: Button? = null
    //private var mBreedFilterButton: Button? = null

    //this variable check if the user is submitting the same query twice so that
    //we don't perform two network calls
    private var isDoubleSubmit = false
    //private var mCardView: CardView? = null
    private var searchFilterType = SEARCH_FILTER_NAME

    private var mPetKeyHashset: HashSet<String>? = null

    //user search query
    private var searchQuery: String ?= null

    private var listener: ValueEventListener ?= null

    private var searchQueryLimit: Int ? = 12

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.activity_pet_query, container, false)
        //firebase instances
        mDatabase = FirebaseDatabase.getInstance()
        mRef = mDatabase!!.getReference(FirebaseValues.FirebaseDatabaseValues.FIREBASE_PETS_ROOT)
        initUIViewsAndVariables(rootView)
        setupFilterTab(rootView)

        return rootView
    }

    /*
    Takes a query from the user to query it against the realtime database. The old query is
    erased and views are changed accordingly. is Refresh scroll determines whether or not the user has
    finished scrolling to the end of the list and it needs to refresh
    */
    private fun submitSearchQuery(string: String,isRefreshScroll: Boolean) {
        if(!isRefreshScroll) {
            mPetArrrayList!!.clear()
            mPetKeyHashset!!.clear()
            mPetAdapter!!.notifyDataSetChanged()
        }
        searchQuery = string.toLowerCase().trim()
        mNoPetsProgressBar!!.visibility = View.VISIBLE
        listener= object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                addPetsFromSnapshot(p0!!)
            }


        }
        mRef!!.limitToFirst(searchQueryLimit!!)
                .orderByChild(searchFilterType)
                .startAt(searchQuery)
                .endAt(searchQuery)
                .addListenerForSingleValueEvent(listener)
    }

    /*
    This is the onClick interface from the PetAdapter to allow onClick handling
     */
    override fun onClick(position: Int) {
        val intent = Intent(activity, PetSearchDetailActivity::class.java)
        intent?.putExtra("pet", mPetArrrayList?.get(position))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        } else {
            // Swap without transition
            startActivity(intent)
        }


    }

    /*
    This method takes in a snapshot and checks its keys to a hashset as to not allow for duplicate values
    it also formats the name and breed of the pet as this data is defaulted to lowercase for search efficiency.
     */
    private fun addPetsFromSnapshot(snapshot: DataSnapshot) {

        for(x in snapshot.children){ // iterates through each child in the snapshot to extract info
            val key = x.key
            if(!mPetKeyHashset!!.contains(key)) {
                mPetKeyHashset!!.add(key)
                val isLost = x.child(PET_FOUND).getValue(Boolean::class.java)
                val name = x.child(PET_NAME).getValue().toString()
                val zip = x.child(PET_ZIP).getValue().toString()
                val breed = x.child(PET_BREED).getValue().toString()
                val description = x.child(PET_DESC).getValue().toString()
                val datelost = x.child(PET_DATE_LOST).getValue().toString()
                val profilePicture = x.child(PET_URL_1).getValue().toString()
                val url2 = x.child(PET_URL_2).getValue().toString()
                val url3 = x.child(PET_URL_3).getValue().toString()
                val userId = x.child(USER_ID).getValue().toString()
                val pet = Pet()
                /*
                Since breed and name are used to filter they are entered into the DB in lowercase forms
                so we are converting them back to a more user friendly mode
                 */
                pet.name = name?.substring(0,1).toUpperCase() + name?.substring(1, name.length)
                if(breed != null)
                    pet.breed = breed?.substring(0, 1).toUpperCase() + breed?.substring(1, breed.length)
                pet.zip = zip
                pet.description = description
                pet.dateLost = datelost
                pet.profileUrlOne = profilePicture
                pet.profileUrlTwo = url2
                pet.profileUrlThree = url3
                pet.userID = userId
                pet.setFoundPet(isLost!!)
                mPetArrrayList!!.add(pet)
                mPetAdapter!!.notifyItemChanged(mPetArrrayList!!.size)
            }
        }
        checkIfPetsFound()
    }


    /*
    Initializes all UI elements and variable instances
     */
    private fun initUIViewsAndVariables(rootView: View) {
        //Variable instances
        mNoPetsFoundTextView = rootView.findViewById(R.id.pet_query_no_pets_textview)
        mPetKeyHashset = HashSet()
        mNoPetsProgressBar = rootView.findViewById(R.id.pet_query_progressbar)
        mSearchView = rootView.findViewById(R.id.pet_query_searchview)
        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                /*
                This ensures the user does not re-submit the same query and hides the filter tab
                so the user can clearly see the list
                 */
                if (!isDoubleSubmit && s.length > 0) {
                    searchQueryLimit = querySearchOriginalLimit
                    submitSearchQuery(s, false)
                    isDoubleSubmit = true
                }
                return false
            }
            /*
            Sets the flag to ensure the user does not re-submit queries
             */
            override fun onQueryTextChange(s: String): Boolean {
                isDoubleSubmit = false
                return false
            }
        })
        mRecyclerView = rootView.findViewById(R.id.pet_query_recyclerview)
        mLayoutManager = LinearLayoutManager(context)
        mRecyclerView!!.layoutManager = mLayoutManager
        mPetArrrayList = ArrayList()

        mPetAdapter = PetAdapter(mPetArrrayList)
        mPetAdapter!!.setOnViewClicked(this)
        mRecyclerView!!.adapter = mPetAdapter
        setOnScrollListener()
    }

    /*
    called on after from the single event listener to ensure the UI reflects the
    query status. Also checks if the size of the array exceeds our threshhold
     */
    private fun checkIfPetsFound() {
        mNoPetsProgressBar!!.visibility = View.GONE
        if (mPetArrrayList!!.size == NO_PETS_FOUND) {
            mNoPetsFoundTextView!!.visibility = View.VISIBLE
        } else {
            mNoPetsFoundTextView!!.visibility = View.GONE
        }

        if(mPetArrrayList!!.size > 45){
            for( x in 0..9){
                mPetArrrayList!!.removeAt(0)
                mPetAdapter!!.notifyItemRemoved(0)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(PET_ARRAY_KEY, mPetArrrayList)
        outState.putString(PET_SEARCH_QUERY_KEY, searchQuery)
        outState.putInt(PET_SEARCH_QUERY_LIMIT_KEY, searchQueryLimit!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            /*
            Saves arraylist for any instance state change
             */
            if(savedInstanceState.containsKey(PET_ARRAY_KEY)) {
                mPetArrrayList = savedInstanceState.getSerializable(PET_ARRAY_KEY) as ArrayList<Pet>
                mPetAdapter = PetAdapter(mPetArrrayList)
                mPetAdapter!!.setOnViewClicked(this)
                mRecyclerView!!.adapter = mPetAdapter
            }
            if(savedInstanceState.containsKey(PET_SEARCH_QUERY_KEY)){
                searchQuery = savedInstanceState.getString(PET_SEARCH_QUERY_KEY)
            }
            if(savedInstanceState.containsKey(PET_SEARCH_QUERY_LIMIT_KEY)){
                searchQueryLimit = savedInstanceState.getInt(PET_SEARCH_QUERY_LIMIT_KEY)
            }

        }
    }

    /*
    Adds a scroll listener to the recycler view in order to detect when the user has reached the end of the list
     */
    private fun setOnScrollListener(){
        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!recyclerView!!.canScrollVertically(1)){
                    searchQueryLimit = searchQueryLimit!!.plus(1!!)
                    submitSearchQuery(searchQuery!!, true)
                }
            }
        })
    }

    /*
    Sets up the filter tab and on select listener
     */
    private fun setupFilterTab(rootView:View){
        rootView.filter_tab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                isDoubleSubmit = false
                when(tab?.position){
                    0 -> searchFilterType = SEARCH_FILTER_NAME
                    1 -> searchFilterType = SEARCH_FILTER_ZIP
                    2 -> searchFilterType = SEARCH_FILTER_BREED
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    companion object {
        val querySearchOriginalLimit = 12
        val PET_SEARCH_QUERY_LIMIT_KEY = "limit"
        val PET_SEARCH_QUERY_KEY = "query"
        val PET_ARRAY_KEY = "arrayList"
        val SEARCH_FILTER_NAME = "name"
        val SEARCH_FILTER_ZIP = "zip"
        val SEARCH_FILTER_BREED = "breed"
        private val NO_PETS_FOUND = 0

        val PET_ZIP = "zip"
        val PET_NAME = "name"
        val PET_FOUND = "isFoundPet"
        val PET_BREED = "breed"
        val PET_DESC = "description"
        val PET_DATE_LOST = "dateLost"
        val PET_URL_1 = "profileUrlOne"
        val PET_URL_2 = "profileUrlTwo"
        val PET_URL_3 = "profileUrlThree"
        val USER_ID = "userID"

    }
}
