package com.example.enduser.lostpetz;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by EndUser on 4/29/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<Message> mMessageList;

    private onPitureClicked onClick;

    public interface onPitureClicked{
        void onPictureClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Message message = mMessageList.get(position);
        holder.mUserName.setText(message.getUserName());
        Glide.with(holder.mProfileImage).applyDefaultRequestOptions(new RequestOptions().circleCrop().error(R.mipmap.ic_launcher_round))
                .load(mMessageList.get(position).getUserProfileUrl()).into(holder.mProfileImage);
        if(message.getPhotoUrl() == null) {
            holder.mPictureMessage.setVisibility(View.GONE);
            holder.mMessage.setVisibility(View.VISIBLE);
            holder.mMessage.setText(message.getMessage());
        }
        else{
            holder.mMessage.setVisibility(View.GONE);
            holder.mPictureMessage.setVisibility(View.VISIBLE);
            Glide.with(holder.mPictureMessage).applyDefaultRequestOptions(new RequestOptions().fitCenter())
                    .load(message.getPhotoUrl()).into(holder.mPictureMessage);
            holder.mPictureMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onPictureClicked(position);
                }
            });
        }

        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    /*
    Overridden so that the recyclerview does not duplicate views
    1)setStableIds
    2)getItemId
    3)getItemViewType
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setOnClick(onPitureClicked onClick){
        this.onClick = onClick;
    }
}
