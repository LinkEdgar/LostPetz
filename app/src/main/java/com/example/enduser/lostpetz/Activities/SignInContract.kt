package com.example.enduser.lostpetz.Activities

import android.content.Context

interface SignInContract{

    interface View{
        fun setProgressBar()
        fun onSuccessfulSignIn()
        fun onSignInFailure()
        fun onPasswordResetSuccess()
        fun onPasswordResetFailure()
    }

    interface Presenter{
        fun isUserSignedIn():Boolean
        fun onSignInClicked(email:String, password: String)
        fun onPasswordResetClicked(email:String)
        fun getDataFromReceiver(key: String,isSignInSuccess: Boolean)
    }

}