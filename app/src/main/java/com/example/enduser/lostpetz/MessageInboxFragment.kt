package com.example.enduser.lostpetz

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.message_inbox_fragement.view.*
import kotlin.collections.ArrayList

/*
This fragment displays a list of messages that the user has
*/


open class MessageInboxFragment: Fragment(), InboxAdapter.onClicked {

    private var mRecylerview: RecyclerView? = null
    private var mInboxData = ArrayList<User>()
    private var mAdapter: InboxAdapter? = null
    private var keyHashSet: HashSet<String> ?= null

    private var user: User? = null

    private var rootView: View? = null

    private val INBOX_KEY  = "inbox_key"
    private val HASH_SET_KEY = "hash_set"

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mRef: DatabaseReference? = null
    private var mUser: FirebaseUser? = null


    //TODO don't load all the data at once

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.message_inbox_fragement, container, false)
        keyHashSet = HashSet()
        if(savedInstanceState == null) {
            Log.e("loading data", "loading..")
            initFirebase()
            searchForChats()
        }
        return rootView
    }

    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
        mRef = FirebaseDatabase.getInstance().getReference("Users")
    }

    private fun initRecyclerView() {
        mRecylerview = rootView!!.message_inbox_fragment_recyclerview
        val layoutManager = LinearLayoutManager(context)
        mRecylerview!!.layoutManager = layoutManager
        mAdapter = InboxAdapter(mInboxData, context, this)
        mRecylerview?.adapter = mAdapter
    }

    /*
    Interface for the adapter to handle onclick of views. Launches intent to switch to MessagingActivity
     */
    override fun onClick(position: Int) {
        //TODO add extra information
        val intent = Intent(context, MessagingActivity::class.java)
        val jointChat = mInboxData[position].chatId
        intent.putExtra("jointChat", jointChat)
        startActivity(intent)
    }

    /*
    Step 1 get the list of chats
     */
    private fun searchForChats(){
        val userId = mUser!!.uid
        mRef!!.child(userId).child("chats").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot?) {
                    getListOfChats(p0!!)
                }

                override fun onCancelled(p0: DatabaseError?) {

                }
        })
    }

    private fun getListOfChats(dataSnapshot: DataSnapshot){
        for(x in dataSnapshot.children){
            val chatId = x.getValue(String::class.java)
            retrieveOtherUsersInfo(chatId!!)
        }
        if(dataSnapshot.childrenCount < 1){
            rootView!!.message_inbox_no_message_textview.visibility = View.VISIBLE
        }
    }

    /*
    Get the other user's info
     */
    private fun retrieveOtherUsersInfo(chatId: String){
        var split = chatId.substring(0, (chatId!!.length)/2)
        if(split.equals(mUser!!.uid))
            split = chatId.substring((chatId!!.length)/2, chatId.length)

        mRef!!.child(split).child("name").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                getOtherUserData(p0!!, chatId)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })
    }

    private fun getOtherUserData(snapShot: DataSnapshot, chatId: String){
        //TODO getProfileUrl
        val user =  User(null, null, null, null, null)
        val name = snapShot.getValue(String::class.java)
        user.userName = name
        user.chatId = chatId

        //retrieve last message
        val lastMessageRef = FirebaseDatabase.getInstance().getReference("Messages")
        lastMessageRef.child(chatId).limitToLast(1).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                getLastMessage(p0!!, user)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }

        })
    }

    private fun getLastMessage(snapShot: DataSnapshot, user: User){
        for(x in snapShot.children){
            val key = user.chatId
            if(!keyHashSet!!.contains(key)) {
                keyHashSet!!.add(key!!)
                val lastMessage = x.child("message").getValue(String::class.java)
                val photoUrl = x.child("photoUrl").getValue(String::class.java)
                user.lastMessage = lastMessage
                if(lastMessage !=  null || photoUrl != null){
                    mInboxData.add(user)
                }
            }
        }
        if(mInboxData.size < 1)
            rootView!!.message_inbox_no_message_textview.visibility = View.VISIBLE
        else
            rootView!!.message_inbox_no_message_textview.visibility = View.GONE
        initRecyclerView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(INBOX_KEY, mInboxData)
        outState.putSerializable(HASH_SET_KEY, keyHashSet)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(INBOX_KEY)){
                mInboxData = savedInstanceState.get(INBOX_KEY)  as ArrayList<User>
                mRecylerview = rootView!!.message_inbox_fragment_recyclerview
                val layoutManager = LinearLayoutManager(context)
                mRecylerview!!.layoutManager = layoutManager
                mAdapter = InboxAdapter(mInboxData, context, this)
                mRecylerview?.adapter = mAdapter
            }
            if(savedInstanceState.containsKey(INBOX_KEY)){
                keyHashSet = savedInstanceState.get(HASH_SET_KEY) as HashSet<String>
            }
        }
    }
}
