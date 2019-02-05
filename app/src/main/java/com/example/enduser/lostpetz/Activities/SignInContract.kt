package com.example.enduser.lostpetz.Activities

import android.content.Context

interface SignInContract{

    interface View{
        fun setProgressBar()
        fun onSuccessfulSignIn()
        fun onSignInFailure()
    }

    interface Presenter{
        fun onSignInClicked(email:String, password: String)
        fun getDataFromReceiver(isSignInSuccess: Boolean)
    }

}