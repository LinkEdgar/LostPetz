package com.example.enduser.lostpetz.Activities

import android.content.Context
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
import com.example.enduser.lostpetz.R
import com.example.enduser.lostpetz.DialogFragments.RegisterDialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.api.ApiException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignInActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth ?= null
    private var mDatabaseRef: DatabaseReference ?= null
    private var mUserName: String ?= null
    private var mPassword: String ?= null
    private var mUser: FirebaseUser ?= null
    private var mGoogleSignInClient: GoogleSignInClient ?= null
    val RC_SIGN_IN = 3141

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_activty)

        enableGoogleSignIn()

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
        signin_google.setOnClickListener{googleSignIn()}
    }

    /*
    user name and password are set from the two input views. The username(email) is trimmed.
    A progressbar takes the place of the submit button until the sign in task is completed.
    If the tasks is successful we switch to the main activity and finish() this task
     */
    private fun signInViaAuth(){
        mUserName = signin_email.text.toString().trim()
        mPassword = signin_password.text.toString()
        if(mUserName != null && mUserName!!.isNotEmpty()) {
            signin_progressbar.visibility = View.VISIBLE
            signin_button.visibility = View.GONE
            mAuth?.signInWithEmailAndPassword(mUserName!!, mPassword!!)?.addOnCompleteListener(this) { task ->
                signin_progressbar.visibility = View.GONE
                signin_button.visibility = View.VISIBLE
                if (task.isSuccessful) {
                    mUser = mAuth?.currentUser
                    addUserToDB()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, R.string.login_fail, Toast.LENGTH_SHORT).show()
                }
            }
        } else Toast.makeText(this, R.string.non_null_email, Toast.LENGTH_LONG).show()

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
        builder.setNegativeButton(R.string.cancel_label, DialogInterface.OnClickListener{ dialogInterface, i -> })
        builder.create()
        builder.show()
    }

    /*
        This method takes a string email address and sends a password reset to it. Verification is done through the firebase
        SDK. A log message is displayed for its success or failure
     */
    private fun passwordRecovery(email: String){
        if(email != null && email.length > 0) {
            mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Password Reset:", "Successful")
                    Toast.makeText(this, R.string.reset_password_success, Toast.LENGTH_LONG).show()
                } else {
                    Log.d("Password Reset:", "Failed")
                    Toast.makeText(this, R.string.reset_password_fail, Toast.LENGTH_LONG).show()
                }
            }
        }else Toast.makeText(this, R.string.non_null_email, Toast.LENGTH_LONG).show()
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
        if(mUser != null) {
           startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun googleSignIn(){
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun enableGoogleSignIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("onActivityResult", "Google sign in falied", e)
            }


        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.getIdToken(), null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("AuthWithGoogle", "signInWithCredential:success")
                        mUser = mAuth!!.getCurrentUser() as FirebaseUser
                        startActivity(Intent(this, MainActivity::class.java))
                        addUserToDB()
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("AuthWithGoogle", "signInWithCredential:failure", task.exception)
                        Toast.makeText(this, R.string.google_signin_fail, Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun addUserToDB(){
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
        val key = getString(R.string.user_name_shared_pref)
            if(!(sharedPref.getString(key, "null").equals(mUser!!.uid))) {
                with(sharedPref.edit()) {
                    putString(getString(R.string.user_name_shared_pref), mUser?.uid)
                    commit()
                }
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users")

                val userRef = mDatabaseRef!!.child(mUser!!.uid)
                userRef.child("email").setValue(mUser!!.email)
                if(mUser!!.displayName != null) {
                    userRef.child("name").setValue(mUser!!.displayName)
                }
            }
    }
}
