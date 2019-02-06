package com.example.enduser.lostpetz.Activities

import android.content.Context
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.example.enduser.lostpetz.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class SignInPresenter(var context: Context, var view:SignInContract.View): SignInContract.Presenter{

    private var firebaseManager:FirebaseManager = FirebaseManager.getInstance()
    private lateinit var mBroadcastReceiver: SignInBroadcastReceiver

    init {
        registerBroadcastReceiver()
        firebaseManager.enableGoogleSignIn(context)
    }

    override fun onSignInClicked(email: String, password: String) {
        if(email.isNotEmpty() && password.isNotEmpty()) {
            view.setProgressBar()
            firebaseManager.signInViaAuth(email, password, context)
        }
    }

    private fun registerBroadcastReceiver(){
        val intentFilter = IntentFilter("SignIn")
        mBroadcastReceiver = SignInBroadcastReceiver(this)
        LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun getDataFromReceiver(key: String,isSuccess: Boolean) {

        when(key){
            "auth" -> {
                view.setProgressBar()
                if(isSuccess)
                    view.onSuccessfulSignIn()
                else
                    view.onSignInFailure()
            }
            "pass_reset" -> {
                if(isSuccess)
                    view.onPasswordResetSuccess()
                else
                    view.onPasswordResetFailure()
            }
            "auth_google" -> {
                view.setProgressBar()
                if(isSuccess)
                    view.onSuccessfulSignIn()
                else
                    view.onSignInFailure()
            }
        }
    }

    override fun onPasswordResetClicked(email: String) {
        if(email != null && email.isNotEmpty())
            firebaseManager.passwordRecovery(email,context)
        else
            Toast.makeText(context, R.string.non_null_email, Toast.LENGTH_LONG).show()
    }

    override fun isUserSignedIn(): Boolean {
        return firebaseManager.user != null
    }

    override fun onGoogleSignInClicked(account:GoogleSignInAccount) {
        firebaseManager.signInViaGoogle(account, context)
    }

    override fun getGoogleSignInClient(): GoogleSignInClient {
        return firebaseManager.getmGoogleSignInClient()
    }


}