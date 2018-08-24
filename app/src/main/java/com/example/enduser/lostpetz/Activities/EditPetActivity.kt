package com.example.enduser.lostpetz.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.enduser.lostpetz.Adapters.EditPetAdapter
import com.example.enduser.lostpetz.CustomObjectClasses.Pet
import com.example.enduser.lostpetz.R
import kotlinx.android.synthetic.main.activity_edit_pet.*

class EditPetActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: EditPetAdapter
    private lateinit var mData: ArrayList<Pet>
    private lateinit var mLayout: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)

        if(supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        initVar()
        loadFakeData()
    }

    private fun initVar(){
        mData = ArrayList()
        mLayout = LinearLayoutManager(this)
        mRecyclerView = recyclerview
        mAdapter = EditPetAdapter(mData,this)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = mLayout
    }

    private fun loadFakeData(){
        for(x in 0..0){
            mData.add(Pet("$x", "Yeet", false))
        }
        mAdapter.notifyDataSetChanged()
    }
}
