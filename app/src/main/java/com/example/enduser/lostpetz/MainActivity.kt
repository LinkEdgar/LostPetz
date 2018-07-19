package com.example.enduser.lostpetz

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

open class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragments()
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
}