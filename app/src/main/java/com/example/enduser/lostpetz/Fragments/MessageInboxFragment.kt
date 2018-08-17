package com.example.enduser.lostpetz.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.enduser.lostpetz.Activities.MessagingActivity
import com.example.enduser.lostpetz.Adapters.InboxAdapter
import com.example.enduser.lostpetz.R
import com.example.enduser.lostpetz.CustomObjectClasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.message_inbox_fragement.*
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
    private val NO_MESSAGES_KEY = "bool"

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mRef: DatabaseReference? = null
    private var mUser: FirebaseUser? = null


    /*
    This fragment loads the 15 latest conversations that the user has in the convo tree
    TODO add time stamp to messages and when the messages are brought in we can change the order of the
    messages based most current. Also add a refresh in case user wants to scroll through more of their messages
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.message_inbox_fragement, container, false)
        keyHashSet = HashSet()
        if(savedInstanceState == null) {
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
        val intent = Intent(context, MessagingActivity::class.java)
        val jointChat = mInboxData[position].chatId
        intent.putExtra("jointChat", jointChat)
        startActivity(intent)
    }

    /*
    Step 1 get the list of chats
     */
    private fun searchForChats(){
        val userId = mUser?.uid
        setProgressbar(true)
        if(userId != null) {
            mRef!!.child(userId).child("chats").limitToLast(15).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot?) {
                    getListOfChats(p0!!)
                }

                override fun onCancelled(p0: DatabaseError?) {

                }
            })
        }
    }

    private fun getListOfChats(dataSnapshot: DataSnapshot){
        for(x in dataSnapshot.children){
            val chatId = x.getValue(String::class.java)
            retrieveOtherUsersInfo(chatId!!)
        }
        if(dataSnapshot.childrenCount < 1){
            rootView!!.message_inbox_no_message_textview.visibility = View.VISIBLE
            setProgressbar(false)
        }
    }

    /*
    Get the other user's info
     */
    private fun retrieveOtherUsersInfo(chatId: String){
        var split = chatId.substring(0, (chatId!!.length)/2)
        if(split.equals(mUser!!.uid))
            split = chatId.substring((chatId!!.length)/2, chatId.length)

        mRef!!.child(split).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                getOtherUserData(p0!!, chatId)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })
    }

    private fun getOtherUserData(snapShot: DataSnapshot, chatId: String){
        val user = User(null, null, null, null, null)
        val name = snapShot.child("name").getValue(String::class.java)
        val profileUrl = snapShot.child("profileUrl").getValue(String::class.java)
        user.profileUrl = profileUrl
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
        setProgressbar(false)
        initRecyclerView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(INBOX_KEY, mInboxData)
        outState.putSerializable(HASH_SET_KEY, keyHashSet)
        if(rootView!!.message_inbox_no_message_textview.visibility == View.VISIBLE){
            outState.putBoolean(NO_MESSAGES_KEY, true)
        }
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
            if(savedInstanceState.containsKey(NO_MESSAGES_KEY)){
                rootView!!.message_inbox_no_message_textview.visibility = View.VISIBLE
            }
        }
    }

    private fun setProgressbar(setOn: Boolean){
        if(setOn)
            rootView?.message_inbox_progressbar?.visibility = View.VISIBLE
        else
            rootView?.message_inbox_progressbar?.visibility = View.GONE

    }
}
