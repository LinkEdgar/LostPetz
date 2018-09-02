package com.example.enduser.lostpetz.Activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.enduser.lostpetz.Adapters.EditPetAdapter
import com.example.enduser.lostpetz.CustomObjectClasses.Pet
import com.example.enduser.lostpetz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_pet.*


class EditPetActivity : AppCompatActivity(), EditPetAdapter.PetAdapterInterface {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: EditPetAdapter
    private lateinit var mData: ArrayList<Pet>
    private lateinit var mLayout: RecyclerView.LayoutManager

    private lateinit var imageUriArray: Array<Uri?>
    private var imagePosition = 0 //used to keep track of the user's image selection
    private var mImage: ImageView ?= null
    private var petPosition = 0


    //firebase
    private lateinit var mRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mChilListener: ValueEventListener

    private val RC_SELECT_IMAGE = 15

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
        imageUriArray = arrayOfNulls(3)
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

    override fun onCancelImageSelected(urlPosition: Int, position: Int, holder: EditPetAdapter.ViewHolder) {
        when(urlPosition){
            0 -> {
                if(mData[position].profileUrlOne != null) {
                    mData[position].profileUrlOne = null
                    Glide.with(holder.imageOne).load(R.drawable.ic_gallery).into(holder.imageOne)
                }
            }
            1 -> {
                if(mData[position].profileUrlTwo != null){
                    mData[position].profileUrlTwo = null
                    Glide.with(holder.imageTwo).load(R.drawable.ic_gallery).into(holder.imageTwo)
                }
            }
            2 -> {
                if(mData[position].profileUrlThree != null){
                    mData[position].profileUrlThree = null
                    Glide.with(holder.imageThree).load(R.drawable.ic_gallery).into(holder.imageThree)
                }
            }
        }
    }

    override fun onPetImageSelectionSelected(imagePosition: Int, holder: EditPetAdapter.ViewHolder, petPosition: Int) {
        this.imagePosition = imagePosition
        this.petPosition = petPosition
        when(imagePosition){
            0 -> mImage = holder.imageOne
            1 -> mImage = holder.imageTwo
            2 -> mImage = holder.imageThree
        }
        startGalleryIntent()
    }


    override fun onSavedButtonClicked() {

    }

    override fun onDeleteClicked(position: Int) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.delete_pet_confirmation)
        builder.setPositiveButton(R.string.confirm_delete_pet, DialogInterface.OnClickListener { dialogInterface, i -> deletePet(position) })
        builder.setNegativeButton(R.string.cancel_label, DialogInterface.OnClickListener{ dialogInterface, i -> })
        builder.create()
        builder.show()
    }

    private fun deletePet(position: Int){
        Toast.makeText(this, "Delete", Toast.LENGTH_LONG)
        mRef.child(mData[position].petID).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Pet deleted", Toast.LENGTH_LONG).show()
            mData.removeAt(position)
            mAdapter.notifyItemRemoved(position)
        }
    }

    override fun onSubmitPet() {

    }

    private fun startGalleryIntent(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RC_SELECT_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK){
            val uri = data!!.data
            Glide.with(mImage!!).load(uri).into(mImage!!)
            when(imagePosition) {
                0 -> {mData[petPosition].profileUrlOne = ""}
                1 -> {mData[petPosition].profileUrlTwo = ""}
                2 -> {mData[petPosition].profileUrlThree = ""}
            }

        }
    }
}
