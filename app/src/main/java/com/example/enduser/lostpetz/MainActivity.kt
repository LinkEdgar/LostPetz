package com.example.enduser.lostpetz

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


open class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //TEST code
        val mPagerAdapter = MainActivityPager(supportFragmentManager)
        main_activity_viewpager.adapter = mPagerAdapter
        main_activity_tablayout.setupWithViewPager(main_activity_viewpager)
    }

    /*
    Viewpager
     */

    open class MainActivityPager(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return 4
        }

        override fun getItem(position: Int): Fragment {
            when(position){
                0 -> return MatchFragment()
                1 -> return  MessageInboxFragment()
                2 -> return  MessageInboxFragment()
                3 -> return  MessageInboxFragment()
                else -> return MatchFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0 -> return "Match"
                1 -> return "Search"
                2 -> return "Add"
                3 -> return "Message"
                else -> return null
        }
    }
    }
}