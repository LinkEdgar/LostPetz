package com.example.enduser.lostpetz

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*

open class MainActivity: AppCompatActivity(){
    //Firebase
    var mAuth: FirebaseAuth ?= null
    var mRef: DatabaseReference ?= null
    var mStorage:FirebaseStorage ?= null

    val RC_PROFILE_PICTURE = 24


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.main_activity_toolbar)
        setSupportActionBar(toolbar)
        yeet.setOnClickListener{ maint_activity_drawerlayout.openDrawer(GravityCompat.START)}
        initFragments()
        initFirebase()


        getUserInfo()

        initDrawerLayout()
    }
    /*
    This method sets up the tab layout, viewpager, and pager adapter
     */
    private fun initFragments(){
        val mPagerAdapter = MainActivityPager(supportFragmentManager)
        main_activity_viewpager.adapter = mPagerAdapter
        main_activity_tablayout.setupWithViewPager(main_activity_viewpager)
        main_activity_tablayout.getTabAt(0)!!.setIcon(R.drawable.ic_search)
        main_activity_tablayout.getTabAt(1)!!.setIcon(R.drawable.ic_add)
        main_activity_tablayout.getTabAt(2)!!.setIcon(R.drawable.ic_message)

    }

    /*
    Viewpager inner class
     */

    open class MainActivityPager(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return 3
        }

        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> PetQueryFragment()
                1 -> AddPetFragment()
                2 -> MessageInboxFragment()
                else -> MatchFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position){
                0 -> "Search"
                1 -> "Add"
                2 -> "Message"
                else -> null
            }
        }
    }

    private fun initDrawerLayout(){
        //set on click listener for profile picture
        maint_activity_navigation_view.getHeaderView(0).header_add_profile.setOnClickListener{changeProfilePicture()}

        maint_activity_navigation_view.setNavigationItemSelectedListener {item: MenuItem ->
            item.isChecked
            maint_activity_drawerlayout.closeDrawers()
            when(item.itemId){
                R.id.nav_sign_out -> { //user signs out user and takes them to login screen
                    mAuth?.signOut()
                    val intent = Intent(this@MainActivity, SignInActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Successfully signed out", Toast.LENGTH_SHORT).show()
                    finish()
                }
                R.id.nav_match ->{ //takes user to match activity
                    startActivity(Intent(this, MatchActivity::class.java))
                }
            }

            true
        }

    }

    private fun initFirebase(){
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().getReference("Users")
        mStorage = FirebaseStorage.getInstance()
    }

    /*
    Handles the case where the navigation drawer is open
     */
    override fun onBackPressed() {
        if(maint_activity_drawerlayout.isDrawerOpen(GravityCompat.START))
            maint_activity_drawerlayout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

    /*
    Takes the username and profile url and sets their profile in the navigation view's header
    If the profile url is empty an default image is substituted instead
     */
    private fun setUserProfile(username: String?, profileUrl: String?){
        val header: View = maint_activity_navigation_view.getHeaderView(0)
        header.header_name.setText(username)
        Glide.with(this).applyDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round).circleCrop()).load(profileUrl).into(header.header_profile_picture)
    }

    /*
    This method gets the user's information to load their name and profile picture to the header in the navigation view
     */
    private fun getUserInfo(){
        val userRef = mRef?.child(mAuth?.currentUser?.uid)
        userRef?.addListenerForSingleValueEvent(object: ValueEventListener{
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
        val selectImageIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
        val profileImage = maint_activity_navigation_view.getHeaderView(0).header_profile_picture
        profileImage.visibility = View.GONE
        val progressbar = maint_activity_navigation_view.getHeaderView(0).header_progressbar
        progressbar.visibility = View.VISIBLE
        uploadTask.addOnFailureListener {
            Toast.makeText(this, R.string.image_upload_failed, Toast.LENGTH_LONG).show()
        }.addOnCompleteListener {
            profileImage.visibility = View.VISIBLE
            progressbar.visibility = View.GONE

        }.addOnSuccessListener { taskSnapshot ->
            val url = taskSnapshot.downloadUrl.toString()
            Glide.with(this).applyDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round).circleCrop())
                    .load(url).into(profileImage)
            addProfileUrlToDB(url)
        }

    }

    /*
    Adds url to user's information
     */
    private fun addProfileUrlToDB(url:String){
        mRef!!.child(mAuth?.currentUser?.uid).child("profileUrl").setValue(url)
    }
}