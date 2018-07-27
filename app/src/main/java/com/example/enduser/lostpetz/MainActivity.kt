package com.example.enduser.lostpetz

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

open class MainActivity: AppCompatActivity(){
    //Firebase
    var mAuth: FirebaseAuth ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(main_activity_toolbar)
        initFragments()
        initFirebase()

        initDrawerLayout()
    }
    /*
    This method sets up the tab layout, viewpager, and pager adapter
     */
    private fun initFragments(){
        val mPagerAdapter = MainActivityPager(supportFragmentManager)
        main_activity_viewpager.adapter = mPagerAdapter
        main_activity_tablayout.setupWithViewPager(main_activity_viewpager)
        main_activity_tablayout.getTabAt(0)!!.setIcon(R.drawable.ic_heart)
        main_activity_tablayout.getTabAt(1)!!.setIcon(R.drawable.ic_search)
        main_activity_tablayout.getTabAt(2)!!.setIcon(R.drawable.ic_add)
        main_activity_tablayout.getTabAt(3)!!.setIcon(R.drawable.ic_message)

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
                0 -> MatchFragment()
                1 -> PetQueryFragment()
                2 -> AddPetFragment()
                3 -> MessageInboxFragment()
                else -> MatchFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position){
                0 -> "Match"
                1 -> "Search"
                2 -> "Add"
                3 -> "Message"
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
}