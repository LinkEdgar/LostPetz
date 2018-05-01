package com.example.enduser.lostpetz;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by EndUser on 4/29/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<Message> mMessageList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        //TODO add support for pictures messages
        public TextView mUserName;
        public TextView mMessage;
        public ImageView mProfileImage;
        public ImageView mPictureMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            mUserName = itemView.findViewById(R.id.messnger_user_name);
            mMessage = itemView.findViewById(R.id.messenger_user_message);
            mProfileImage = itemView.findViewById(R.id.messenger_user_profile_image);
            mPictureMessage = itemView.findViewById(R.id.messenger_picture_message_option);
        }
    }

    public MessageAdapter(ArrayList<Message> arrayList){
        mMessageList = arrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View messageView = inflater.inflate(R.layout.message_container,parent,false);

        ViewHolder viewHolder = new ViewHolder(messageView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        holder.mProfileImage.setImageResource(R.mipmap.ic_launcher);
        holder.mUserName.setText(message.getUserName());
        if(message.getPhotoUrl() == null) {
            holder.mMessage.setVisibility(View.VISIBLE);
            holder.mMessage.setText(message.getMessage());
        }
        else{
            holder.mMessage.setVisibility(View.GONE);
            holder.mPictureMessage.setVisibility(View.VISIBLE);
            //TODO load image here
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }




}
