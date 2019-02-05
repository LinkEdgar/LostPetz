package com.example.enduser.lostpetz.Activities

import android.content.Context
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast

class SignInPresenter(var context: Context, var view:SignInContract.View): SignInContract.Presenter{

    private var firebaseManager:FirebaseManager = FirebaseManager.getInstance()
    private lateinit var mBroadcastReceiver: SignInBroadcastReceiver

    init {
        registerBroadcastReceiver()
    }

    override fun onSignInClicked(email: String, password: String) {
        if(email.isNotEmpty() && password.isNotEmpty()) {
            view.setProgressBar()
            firebaseManager.signInViaAuth(email, password, context)
        }
    }

    private fun registerBroadcastReceiver(){
        val intentFilter = IntentFilter("yeet")
        mBroadcastReceiver = SignInBroadcastReceiver(this)
        LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun getDataFromReceiver(isSignInSuccess: Boolean) {
        view.setProgressBar()
        if(isSignInSuccess)
            view.onSuccessfulSignIn()
        else view.onSignInFailure()
    }


}