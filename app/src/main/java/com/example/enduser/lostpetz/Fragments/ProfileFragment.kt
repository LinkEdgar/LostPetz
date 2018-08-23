package com.example.enduser.lostpetz.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.enduser.lostpetz.Activities.SignInActivity
import com.example.enduser.lostpetz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment(), View.OnClickListener{

    private var mRef: DatabaseReference ?= null
    private var mAuth: FirebaseAuth ?= null
    private var mStorage: FirebaseStorage ?= null

    //
    private lateinit var mAddButton: ImageView
    private lateinit var mProfilePicture: ImageView
    private lateinit var mProgressbar: ProgressBar
    private lateinit var mUserName: TextView
    private lateinit var mSignOutTextView: TextView
    private lateinit var mSignOutIcon: ImageView

    //
    private var userProfileUrl: String ?= null

    //keys for bundle
    val usernameKey = "username"
    val userProfileUrlKey = "urlKey"

    val RC_PROFILE_PICTURE = 24// as said by patrick when he was asked for his name by Mrs. Puff

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        initUI(rootView)
        initFirebase()
        if(savedInstanceState == null) {
            getUserInfo()
        }
        return rootView
    }

    private fun initUI(rootView: View){
        mAddButton = rootView.add_pet
        mAddButton.setOnClickListener(this)
        mProfilePicture = rootView.profile_picture
        mProgressbar = rootView.progressbar
        mUserName = rootView.user_name_texView
        mSignOutTextView = rootView.sign_out_textview
        mSignOutTextView.setOnClickListener(this)
        mSignOutIcon = rootView.signout_icon
        mSignOutIcon.setOnClickListener(this)

    }

    private fun initFirebase(){
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().getReference("Users")
        mStorage = FirebaseStorage.getInstance()
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.add_pet -> changeProfilePicture()
            R.id.signout_icon -> signOut()
            R.id.sign_out_textview -> signOut()

        }
    }

    /*
   Takes the username and profile url and sets their profile in the navigation view's header
   If the profile url is empty an default image is substituted instead
    */
    private fun setUserProfile(username: String?, profileUrl: String?){
        userProfileUrl = profileUrl
        Glide.with(this).applyDefaultRequestOptions(RequestOptions().circleCrop().error(R.drawable.default_profile)
                .placeholder(R.drawable.default_profile))
                .load(userProfileUrl).into(mProfilePicture) //loads user's profile picture
        mUserName.setText(username)
    }

    /*
    This method gets the user's information to load their name and profile picture to the header in the navigation view
     */
    private fun getUserInfo(){
        val userRef = mRef?.child(mAuth?.currentUser?.uid)
        userRef?.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                val username = p0?.child("name")?.getValue(String::class.java)
                val profileUrl = p0?.child("profileUrl")?.getValue(String::class.java)
                setUserProfile(username,profileUrl)


            }
            override fun onCancelled(p0: DatabaseError?) {}

        })
    }

    /*
    Starts an intent to open the gallery
     */
    private fun changeProfilePicture(){
        val selectImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(selectImageIntent, RC_PROFILE_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_PROFILE_PICTURE && resultCode == Activity.RESULT_OK
                && null != data) {
            val imageUri = data.data
            uploadProfilePicture(imageUri)
        }
    }

    /*
    Uploads and sets the profile picture the user chooses from their gallery.
    If it is successful the profile picture is set and the url is sent to the database. Otherwise a toast
    is displayed
     */
    private fun uploadProfilePicture(imageUri: Uri){
        val uploadTask: UploadTask = mStorage?.getReference("ProfilePictures/"+ mAuth!!.currentUser!!.uid)!!.putFile(imageUri)
        mProfilePicture!!.visibility = View.GONE
        mProgressbar!!.visibility = View.VISIBLE
        uploadTask.addOnFailureListener {
            Toast.makeText(context, R.string.image_upload_failed, Toast.LENGTH_LONG).show()
        }.addOnCompleteListener {
            mProfilePicture!!.visibility = View.VISIBLE
            mProgressbar!!.visibility = View.GONE
        }.addOnSuccessListener { taskSnapshot ->
            val url = taskSnapshot.downloadUrl.toString()
          Glide.with(this).applyDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round).circleCrop())
                    .load(url).into(mProfilePicture!!)
            addProfileUrlToDB(url)
        }

    }

    /*
    Adds url to user's information
     */
    private fun addProfileUrlToDB(url:String){
        mRef!!.child(mAuth?.currentUser?.uid).child("profileUrl").setValue(url)
    }

    private fun signOut(){
        mAuth?.signOut()
        val intent = Intent(activity, SignInActivity::class.java)
        startActivity(intent)
        Toast.makeText(context, "Successfully signed out", Toast.LENGTH_SHORT).show()
        activity!!.finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(usernameKey, mUserName.text.toString())
        outState.putString(userProfileUrlKey, userProfileUrl)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(usernameKey))
                mUserName.text = savedInstanceState.getString(usernameKey)
            if(savedInstanceState.containsKey(userProfileUrlKey)) {
                userProfileUrl = savedInstanceState.getString(userProfileUrlKey)
                Glide.with(this).applyDefaultRequestOptions(RequestOptions().circleCrop().error(R.mipmap.ic_launcher_round))
                        .load(userProfileUrl).into(mProfilePicture)
            }
        }
    }
}