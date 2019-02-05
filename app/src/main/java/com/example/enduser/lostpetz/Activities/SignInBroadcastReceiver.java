package com.example.enduser.lostpetz.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SignInBroadcastReceiver extends BroadcastReceiver{

    private SignInContract.Presenter mSignInCallback;

    public SignInBroadcastReceiver(SignInContract.Presenter callback){ mSignInCallback = callback;}

    @Override
    public void onReceive(Context context, Intent intent) {
            Boolean isSuccessful = intent.getBooleanExtra("value",false);
            mSignInCallback.getDataFromReceiver(isSuccessful);
    }
}
