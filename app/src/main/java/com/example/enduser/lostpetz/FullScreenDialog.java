package com.example.enduser.lostpetz;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class FullScreenDialog extends DialogFragment{

    private ImageView mImageView;
    private ImageView mCancelButton;
    private String imageUrl;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.message_full_screen,container,false);
        initUi(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    public void setImageUrl(String url){
        imageUrl = url;
    }

    private void initUi(View view){
        mCancelButton = view.findViewById(R.id.message_full_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        mImageView = (ImageView) view.findViewById(R.id.message_full_screen_image);
        Glide.with(getContext()).applyDefaultRequestOptions(new RequestOptions().fitCenter().error(R.mipmap.ic_launcher)).load(imageUrl).into(mImageView);
    }
}
