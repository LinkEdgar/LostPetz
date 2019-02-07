package com.example.enduser.lostpetz.Activities

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface SignInContract{

    interface View{
        fun setProgressBar()
        fun onSuccessfulSignIn()
        fun onSignInFailure()
        fun onPasswordResetSuccess()
        fun onPasswordResetFailure()
    }

    interface Presenter{
        fun getGoogleSignInClient():GoogleSignInClient
        fun isUserSignedIn():Boolean
        fun onSignInClicked(email:String, password: String)
        fun onPasswordResetClicked(email:String)
        fun onGoogleSignInClicked(account:GoogleSignInAccount)
        fun getDataFromReceiver(key: String,isSuccess: Boolean)
    }

}