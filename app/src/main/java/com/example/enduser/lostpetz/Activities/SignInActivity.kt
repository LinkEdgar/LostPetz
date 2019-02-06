package com.example.enduser.lostpetz.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_in_activty.*
import android.widget.LinearLayout
import android.widget.EditText
import com.example.enduser.lostpetz.R
import com.example.enduser.lostpetz.DialogFragments.RegisterDialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException


class SignInActivity : AppCompatActivity(), SignInContract.View {

    val RC_SIGN_IN = 3141


    private lateinit var mPresenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_activty)

        mPresenter = SignInPresenter(this, this)
        initUi()
    }
    //sets on click listeners for views
    private fun initUi(){
        signin_button.setOnClickListener{
            mPresenter.onSignInClicked(signin_email.text.toString().trim(), signin_password.text.toString())}
        signin_forgot_password.setOnClickListener{recoverPassword()}
        signin_register.setOnClickListener{beginRegister()}
        signin_google.setOnClickListener{googleSignIn()}
    }

    /*
    Creates a dialog builder to accept an email for resetting the user's password
     */
    private fun recoverPassword(){
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.reset_password_dialog_title)
        val input = EditText(this)
        input.setHint(R.string.email_hint_label)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        builder.setView(input)
        builder.setPositiveButton(R.string.reset_password_label) { dialogInterface, i -> mPresenter.onPasswordResetClicked(input.text.toString().trim())}
        builder.setNegativeButton(R.string.cancel_label) { dialogInterface, i -> }
        builder.create()
        builder.show()
    }

    /*
    This method launches a dialog fragment
     */
    private fun beginRegister(){
        val registerFragment = RegisterDialogFragment()
        registerFragment.show(supportFragmentManager, "RegisterFrag")
    }

    /*
    Checks if the user has previously signed in
     */
    override fun onStart() {
        super.onStart()
        if(mPresenter.isUserSignedIn()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun googleSignIn(){
        val signInIntent = mPresenter.getGoogleSignInClient().signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                mPresenter.onGoogleSignInClicked(account)
            } catch (e: ApiException) {
                Log.w("onActivityResult", "Google sign in falied", e)
                Toast.makeText(this, R.string.google_signin_fail, Toast.LENGTH_LONG)
            }
        }
    }

    override fun onPasswordResetSuccess() {
        Log.d("Password Reset:", "Successful")
        Toast.makeText(this, R.string.reset_password_success, Toast.LENGTH_LONG).show()
    }

    override fun onPasswordResetFailure() {
        Log.d("Password Reset:", "Failed")
        Toast.makeText(this, R.string.reset_password_fail, Toast.LENGTH_LONG).show()
    }

    override fun onSuccessfulSignIn() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onSignInFailure() { Toast.makeText(this, R.string.login_fail, Toast.LENGTH_LONG).show() }

    override fun setProgressBar() {
        if(signin_progressbar.visibility == View.VISIBLE){
            signin_progressbar.visibility = View.GONE
        }else signin_progressbar.visibility = View.VISIBLE
    }
}
