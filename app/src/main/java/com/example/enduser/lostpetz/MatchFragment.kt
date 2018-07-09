package com.example.enduser.lostpetz

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daprlabs.aaron.swipedeck.SwipeDeck
import kotlinx.android.synthetic.main.fragment_match.view.*

open class MatchFragment: Fragment(), MatchAdapter.onClicked{

    var cardSwipe: SwipeDeck? = null

    //TODO remove the bookmark on top for container
    //TODO make sure cardswipe works with dataset
    //TODO enable back click interface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_match, container, false)
        val dataSet = ArrayList<MatchInfo>()
        dataSet.add(MatchInfo("1","1"))
        dataSet.add(MatchInfo("2","2"))
        dataSet.add(MatchInfo("3","3"))
        dataSet.add(MatchInfo("4","4"))

        cardSwipe = rootView.swipe_deck
        val matchAdapter = MatchAdapter(dataSet,context!!, this)
        cardSwipe?.setAdapter(matchAdapter)
        cardSwipe!!.SWIPE_ENABLED = false
        return rootView
    }

    override fun backClicked() {
        //cardSwipe.add
    }

    override fun nextClicked() {
        cardSwipe!!.swipeTopCardLeft(180)
    }
}