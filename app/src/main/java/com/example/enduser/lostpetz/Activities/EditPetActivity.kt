package com.example.enduser.lostpetz.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.enduser.lostpetz.Adapters.EditPetAdapter
import com.example.enduser.lostpetz.CustomObjectClasses.Pet
import com.example.enduser.lostpetz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_pet.*
import java.text.FieldPosition

class EditPetActivity : AppCompatActivity(), EditPetAdapter.PetAdapterInterface {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: EditPetAdapter
    private lateinit var mData: ArrayList<Pet>
    private lateinit var mLayout: RecyclerView.LayoutManager


    //firebase
    private lateinit var mRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mChilListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)

        if(supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        initVar()
        initFirebase()
        //loadFakeData()
    }

    private fun initVar(){
        mData = ArrayList()
        mLayout = LinearLayoutManager(this)
        mRecyclerView = recyclerview
        mAdapter = EditPetAdapter(mData,this, this)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = mLayout
    }

    private fun loadFakeData(){

        //TODO delete
        for(x in 0..0){
            mData.add(Pet("$x", "Yeet", false))
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun initFirebase(){
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().getReference("Pets")
        mChilListener = object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                addDataToAdapter(p0!!)
            }

            override fun onCancelled(p0: DatabaseError?) {}

        }
        val currentUser = mAuth.currentUser!!.uid
        mRef.orderByChild("userID").startAt(currentUser)
                .endAt(currentUser)
                .addListenerForSingleValueEvent(mChilListener)
    }

    private fun addDataToAdapter(snapshot: DataSnapshot){
        Log.e("datasnap shot", "--> $snapshot")
        for(x in snapshot.children){
            val pet = x.getValue(Pet::class.java)
            mData.add(pet!!)
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun onCancelImageSelected(urlPosition: Int, position: Int) {
        when(urlPosition){
            0 -> {
                if(mData[position].profileUrlOne != null) {
                    mData[position].profileUrlOne = null
                    mAdapter.notifyItemChanged(position)
                }
            }
            1 -> {
                if(mData[position].profileUrlTwo != null){
                    mData[position].profileUrlTwo = null
                    mAdapter.notifyItemChanged(position)
                }
            }
            2 -> {
                if(mData[position].profileUrlThree != null){
                    mData[position].profileUrlThree = null
                    mAdapter.notifyItemChanged(position)
                }
            }
        }
    }

    override fun onPetImageSelectionSelected() {

    }

    override fun onSavedButtonClicked() {

    }

    override fun onDeleteClicked() {

    }
}
