package com.example.enduser.lostpetz.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.transition.Explode
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.enduser.lostpetz.Fragments.AddPetFragment
import com.example.enduser.lostpetz.Fragments.MessageInboxFragment
import com.example.enduser.lostpetz.Fragments.PetQueryFragment
import com.example.enduser.lostpetz.Fragments.ProfileFragment
import com.example.enduser.lostpetz.R
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
        enableTranisitions()
        setContentView(R.layout.activity_main)
        initFragments()
        initFirebase()

    }
    /*
    This method sets up the tab layout, viewpager, and pager adapter
     */
    private fun initFragments(){
        val pagerAdapter = MainActivityPager(supportFragmentManager)
        main_activity_viewpager.adapter = pagerAdapter
        main_activity_viewpager.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                return true
            }

        })
        main_activity_tablayout.setupWithViewPager(main_activity_viewpager)
        main_activity_tablayout.getTabAt(0)!!.setIcon(R.drawable.ic_search)
        main_activity_tablayout.getTabAt(1)!!.setIcon(R.drawable.ic_add)
        main_activity_tablayout.getTabAt(2)!!.setIcon(R.drawable.ic_message)
        main_activity_tablayout.getTabAt(3)!!.setIcon(R.drawable.ic_profile)
        main_activity_viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageSelected(position: Int) {
                when(position){

                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

    }

    /*
    Viewpager inner class
     */

    open class MainActivityPager(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return 4
        }

        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> PetQueryFragment()
                1 -> AddPetFragment()
                2 -> MessageInboxFragment()
                3 -> ProfileFragment()
                else -> AddPetFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position){
                0 -> "Search"
                1 -> "Add"
                2 -> "Message"
                3 -> "Profile"
                else -> null
            }
        }
    }

    private fun initFirebase(){
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().getReference("Users")
        mStorage = FirebaseStorage.getInstance()
    }
    private fun enableTranisitions(){
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            // set an exit transition
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                exitTransition = Explode()
            }
        }
    }
}