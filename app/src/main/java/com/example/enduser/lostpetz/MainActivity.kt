package com.example.enduser.lostpetz

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
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
    fun initFragments(){
        val mPagerAdapter = MainActivityPager(supportFragmentManager)
        main_activity_viewpager.adapter = mPagerAdapter
        main_activity_tablayout.setupWithViewPager(main_activity_viewpager)
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
                1 -> MessageInboxFragment()
                2 -> MessageInboxFragment()
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