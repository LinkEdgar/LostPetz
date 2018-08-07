package com.example.enduser.lostpetz.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.enduser.lostpetz.R
import com.example.enduser.lostpetz.CustomObjectClasses.User
import kotlinx.android.synthetic.main.inbox_container.view.*


open class InboxAdapter(val data: ArrayList<User>, val context: Context?, val onClick: onClicked): RecyclerView.Adapter<InboxAdapter.InboxViewHolder>() {

    interface onClicked{
        fun onClick(poisiton: Int)
    }

    override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
        //test data
        holder.profilePictureIv?.setImageResource(R.mipmap.ic_launcher_round)
        Glide.with(holder.profilePictureIv!!).applyDefaultRequestOptions(RequestOptions().circleCrop()
                .centerCrop().error(R.drawable.default_profile_picture))
                .load(data.get(position).profileUrl).into(holder.profilePictureIv)
        holder.senderNameTv?.setText(data[position].userName)
        val lastMessage = data[position].lastMessage
        if(lastMessage.equals("null") || lastMessage == null)
            holder.lastMessageTv?.setText("Picture message")
        else
            holder.lastMessageTv?.setText(data[position].lastMessage)

        holder.view?.setOnClickListener{onClick.onClick(position)}
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder {
        val viewHolder = InboxViewHolder(LayoutInflater.from(context).inflate(R.layout.inbox_container, parent, false))
        return viewHolder
    }

    open class InboxViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val profilePictureIv = itemView?.inbox_container_profile_picture
        val senderNameTv = itemView?.inbox_container_name
        val lastMessageTv = itemView?.inbox_container_last_message
        val view = itemView
    }
}