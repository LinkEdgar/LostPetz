package com.example.enduser.lostpetz

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in_activty.*
import android.widget.LinearLayout
import android.widget.EditText




class SignInActivty : AppCompatActivity() {

    var mAuth: FirebaseAuth ?= null
    var mUserName: String ?= null
    var mPassword: String ?= null
    var mUser: FirebaseUser ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_activty)
        //initiates firebase auth
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth?.currentUser

        initUi()
    }
    //sets on click listeners for views
    private fun initUi(){
        signin_button.setOnClickListener{signInViaAuth()}
        signin_forgot_password.setOnClickListener{recoverPassword()}
        signin_register.setOnClickListener{beginRegister()}
    }

    /*
    user name and password are set from the two input views. The username(email) is trimmed.
    A progressbar takes the place of the submit button until the sign in task is completed.
    If the tasks is successful we switch to the main activity and finish() this task
     */
    private fun signInViaAuth(){
        mUserName = signin_email.text.toString().trim()
        mPassword = signin_password.text.toString()
        signin_progressbar.visibility = View.VISIBLE
        signin_button.visibility = View.GONE
        mAuth?.signInWithEmailAndPassword(mUserName!!,mPassword!!)?.addOnCompleteListener(this){
            task ->
            signin_progressbar.visibility = View.GONE
            signin_button.visibility = View.VISIBLE
            if(task.isSuccessful){
                mUser = mAuth?.currentUser
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, R.string.login_fail, Toast.LENGTH_SHORT).show()
            }
        }

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
        builder.setPositiveButton(R.string.reset_password_label, DialogInterface.OnClickListener { dialogInterface, i -> passwordRecovery(input.text.toString().trim()) })
        builder.setNegativeButton(R.string.cancel_label, DialogInterface.OnClickListener{dialogInterface, i -> })
        builder.create()
        builder.show()
    }

    private fun passwordRecovery(email: String){
        mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener(this){
            task ->
            if(task.isSuccessful){
                Log.d("Password Reset:", "Successful")
                Toast.makeText(this, R.string.reset_password_success, Toast.LENGTH_SHORT).show()
            }else{
                Log.d("Password Reset:", "Failed")
                Toast.makeText(this, R.string.reset_password_fail,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun beginRegister(){
        //TODO implement decide whether it would be activity or frag
        Toast.makeText(this, "Register clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        //TODO remove commented code
        //if(mUser != null)
            //startActivity(Intent(this, MainActivity::class.java))
    }
}
