package com.example.enduser.lostpetz.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.enduser.lostpetz.Adapters.EditPetAdapter
import com.example.enduser.lostpetz.CustomObjectClasses.Pet
import com.example.enduser.lostpetz.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
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
    private lateinit var mStorage: FirebaseStorage
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
    
    private fun initFirebase(){
        mStorage = FirebaseStorage.getInstance()
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
                if(mData[position].profileUrlOne != null || mData[position].uriOne != null) {
                    mData[position].profileUrlOne = null
                    mData[position].uriOne = null
                    Glide.with(holder.imageOne).load(R.drawable.ic_gallery).into(holder.imageOne)
                }
            }
            1 -> {
                if(mData[position].profileUrlTwo != null || mData[position].uriTwo != null){
                    mData[position].profileUrlTwo = null
                    mData[position].uriTwo = null
                    Glide.with(holder.imageTwo).load(R.drawable.ic_gallery).into(holder.imageTwo)
                }
            }
            2 -> {
                if(mData[position].profileUrlThree != null || mData[position].uriThree != null){
                    mData[position].profileUrlThree = null
                    mData[position].uriThree = null
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
        mRef.child(mData[position].petID).removeValue().addOnSuccessListener {
            Toast.makeText(this, R.string.pet_deleted_success, Toast.LENGTH_LONG).show()
            mData.removeAt(position)
            mAdapter.notifyItemRemoved(position)
        }.addOnFailureListener{
            Toast.makeText(this,R.string.pet_deleted_failure, Toast.LENGTH_LONG).show()
        }
    }

    override fun onSubmitPet(pet: Pet) {
        if(isValidPet(pet)){
            val count = imagesToUploadCount(pet)
            if(count > 0){
                //Upload pictures
                uploadPictures(pet, count)

            }else {
               //submit pet without uploading pictures
                uploadPet(pet)
            }
        }
        else{
            Toast.makeText(this, R.string.required_fields_fail, Toast.LENGTH_LONG).show()
        }
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
                0 -> {
                    mData[petPosition].uriOne = uri
                    mData[petPosition].profileUrlOne = ""
                }
                1 -> {
                    mData[petPosition].uriTwo = uri
                    mData[petPosition].profileUrlTwo = ""
                }
                2 -> {
                    mData[petPosition].uriThree = uri
                    mData[petPosition].profileUrlThree = ""
                }
            }

        }
    }
    private fun isValidPet(pet: Pet): Boolean{
        val zip = pet.zip
        val name = pet.name
        return name.isNotEmpty() && name.length >= 1 && zip.isNotEmpty() && zip.length == 5
    }

    //takes a pet object and counts how many images are available to upload
    private fun imagesToUploadCount(pet:Pet): Int{
        var count = 0
        if(pet.uriOne != null){
            count++
        }
        if(pet.uriTwo != null){
            count++
        }
        if(pet.uriThree != null){
            count++
        }
        return count
    }
    //uploads pet data to firebase
    private fun uploadPet(pet: Pet){
        mRef.child(pet.petID).setValue(pet).addOnSuccessListener {
            Toast.makeText(this,R.string.edit_pet_update_success, Toast.LENGTH_LONG).show()

        }.addOnFailureListener{
            Toast.makeText(this,R.string.edit_pet_update_success, Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadPictures(pet: Pet, imageCount: Int){

        //TODO --> Test that uploading and changing pet information works properly 
        var count = 0
        val uriMap = createUriHashMap(pet)
        val dialog = ProgressDialog(this)
        dialog.setTitle("Uploading ...")
        for (x in 0..uriMap.size) {
                if(uriMap.containsKey(x)) {
                    count++
                    dialog.show()
                    //Case 2
                    if (count == imageCount) {

                        val uploadTask = mStorage.getReference("petImages/" + uriMap[x]!!.getLastPathSegment()).putFile(uriMap[x]!!)
                        uploadTask.addOnFailureListener { Log.d("Upload Image", "failed to upload image") }.addOnCompleteListener {
                            pet.uriOne = null
                            pet.uriTwo = null
                            pet.uriThree = null
                            dialog.dismiss()
                            uploadPet(pet)


                        }.addOnSuccessListener { taskSnapshot ->
                            setPetUrl(x, taskSnapshot.downloadUrl!!.toString(), pet)
                        }

                    } else {
                        //Case 1
                        dialog.show()
                        val uploadTask = mStorage.getReference("petImages/" + uriMap[x]!!.getLastPathSegment()).putFile(uriMap[x]!!)
                        uploadTask.addOnFailureListener { Log.d("Upload Image", "failed to upload image") }.addOnSuccessListener { taskSnapshot ->
                            setPetUrl(x, taskSnapshot.downloadUrl!!.toString(), pet)
                        }
                    }
                }
        }
    }

    //creates a hashmap of uris
    private fun createUriHashMap(pet:Pet): HashMap<Int, Uri>{
        val map: HashMap<Int, Uri> = HashMap()
        if(pet.uriOne != null){
            map.put(0, pet.uriOne)
        }
        if(pet.uriTwo != null){
            map.put(1, pet.uriTwo)
        }
        if(pet.uriThree != null){
            map.put(2, pet.uriThree)
        }

        return map

    }
    //sets pet url based on position
    private fun setPetUrl(position:Int, url: String, pet: Pet ){
        when(position){
            1 -> {pet.profileUrlOne = url}
            2 -> {pet.profileUrlTwo = url}
            3 -> {pet.profileUrlThree = url}
        }
    }
}
