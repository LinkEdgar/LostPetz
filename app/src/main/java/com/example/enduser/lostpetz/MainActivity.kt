package com.example.enduser.lostpetz

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*

open class MainActivity: AppCompatActivity(){
    //Firebase
    var mAuth: FirebaseAuth ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.main_activity_toolbar)
        setSupportActionBar(toolbar)
        yeet.setOnClickListener{ maint_activity_drawerlayout.openDrawer(GravityCompat.START)}
        initFragments()
        initFirebase()


        setUserProfile()

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

        maint_activity_navigation_view.setNavigationItemSelectedListener {item: MenuItem ->
            item.isChecked
            maint_activity_drawerlayout.closeDrawers()
            when(item.itemId){
                R.id.nav_sign_out -> {
                    mAuth?.signOut()
                    val intent = Intent(this@MainActivity, SignInActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Successfully signed out", Toast.LENGTH_SHORT).show()
                    finish()
                }
                R.id.nav_match ->{
                    startActivity(Intent(this, MatchActivity::class.java))
                }
            }

            true
        }

    }

    private fun initFirebase(){
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onBackPressed() {
        if(maint_activity_drawerlayout.isDrawerOpen(GravityCompat.START))
            maint_activity_drawerlayout.closeDrawer(GravityCompat.START)
        else super.onBackPressed();
    }

    private fun setUserProfile(){
        //TODO get user info and load it
        //TODO set profile picture configuration
        val header: View = maint_activity_navigation_view.getHeaderView(0)
        Glide.with(this).applyDefaultRequestOptions(RequestOptions().error(R.mipmap.ic_launcher_round).circleCrop()).load("").into(header.header_profile_picture)
    }
}