package com.example.enduser.lostpetz

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.message_inbox_fragement.view.*
import java.util.*

    /*
   This fragment displays a list of messages that the user has
    */


open class MessageInboxFragment: Fragment(), InboxAdapter.onClicked{

    var mRecylerview: RecyclerView ?= null
    val mInboxData = ArrayList<Message>()
    var mAdapter: InboxAdapter ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.message_inbox_fragement, container, false)
        initRecyclerView(rootView)
        return rootView
    }

    private fun initRecyclerView(rootView: View){
        mRecylerview = rootView.message_inbox_fragment_recyclerview
        val layoutManager = LinearLayoutManager(context)
        mRecylerview!!.layoutManager = layoutManager

        //TODO remove local test data
        mInboxData.add(Message("Username", "Yeet", null, "photoUrl"))
        mInboxData.add(Message("Username", "Yeet", null, "photoUrl"))
        mInboxData.add(Message("Username", "Yeet", null, "photoUrl"))
        mInboxData.add(Message("Username", "Yeet", null, "photoUrl"))
        mInboxData.add(Message("Username", "Yeet", null, "photoUrl"))
        mInboxData.add(Message("Username", "Yeet", null, "photoUrl"))
        mAdapter = InboxAdapter(mInboxData, context, this)
        mRecylerview?.adapter = mAdapter
    }

    /*
    Interface for the adapter to handle onclick of views. Launches intent to switch to MessagingActivity
     */
    override fun onClick(poisiton: Int) {
        //TODO add extra information
        val toast = Toast.makeText(context, "Position $poisiton clicked", Toast.LENGTH_SHORT)
        toast.show()
        val intent = Intent(activity, MessagingActivity::class.java)
        startActivity(intent)
    }
}
