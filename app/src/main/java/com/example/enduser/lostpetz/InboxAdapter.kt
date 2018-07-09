package com.example.enduser.lostpetz

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.inbox_container.view.*


open class InboxAdapter(val data: ArrayList<Message>, val context: Context?): RecyclerView.Adapter<InboxAdapter.InboxViewHolder>() {

    override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
        //test data
        holder.profilePictureIv?.setImageResource(R.mipmap.ic_launcher_round)
        holder.senderNameTv?.setText("Name goes here")
        holder.lastMessageTv?.setText("Last message goes here ")
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder {
        val viewHolder = InboxViewHolder(LayoutInflater.from(context).inflate(R.layout.inbox_container,parent, false))
        return viewHolder
    }

    open class InboxViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val profilePictureIv = itemView?.inbox_container_profile_picture
        val senderNameTv = itemView?.inbox_container_name
        val lastMessageTv = itemView?.inbox_container_last_message
    }

}