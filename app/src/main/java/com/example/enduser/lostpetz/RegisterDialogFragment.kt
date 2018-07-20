package com.example.enduser.lostpetz

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_register.*
import kotlinx.android.synthetic.main.dialog_register.view.*

open class RegisterDialogFragment: DialogFragment(), View.OnClickListener{

    private var mAuth: FirebaseAuth ?= null
    private var mDatabase: DatabaseReference ?= null

    private var mPassword: String ?= null
    private var mConfirmPassword: String ?= null
    private var mEmail: String ?= null
    private var mName: String ?= null
    private var mUser: FirebaseUser ?= null


    //TODO register user
    //TODO add name entry

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_register, container, false)
        //instantiates firebase
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        initUI(rootView)
        return rootView
    }

    //registers onclick listeners
    private fun initUI(rootView: View){
        rootView.register_cancel.setOnClickListener{onClick(register_cancel)}
        rootView.register_submit.setOnClickListener{onClick(register_submit)}
    }

    //gives the fragment full width
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.register_cancel -> dialog.dismiss()
            R.id.register_submit -> registerUser()

        }
    }

    /*
    If the users credentials meet all the requirements then the user is signed in
    Requirements
    1) password length > 6
    2) email != null or length of < 1
    3) passwords match
    4) name length > 3 chars
     */
    private fun registerUser(){
        mPassword = register_password.text.toString()
        mConfirmPassword = register_confirm_password.text.toString()
        mEmail = register_email.text.toString().trim()
        mName = register_name.text.toString().trim()
        if(mName!!.length > 3) {
            if (mEmail != null && mEmail!!.isNotEmpty()) {
                if (passwordVerification(mPassword!!, mConfirmPassword!!)) {
                    mAuth?.createUserWithEmailAndPassword(mEmail!!, mPassword!!)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("registerUser", "Successfully registered user")
                            mUser = mAuth!!.currentUser;
                            addUserToDB(mUser!!)
                            Toast.makeText(context, R.string.register_success, Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            Log.d("registerUser", "Failed to register user")
                            Toast.makeText(context, R.string.register_fail, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else Toast.makeText(context, R.string.password_match_fail, Toast.LENGTH_SHORT).show()
            } else Toast.makeText(context, R.string.non_null_email, Toast.LENGTH_SHORT).show()
        }else Toast.makeText(context, R.string.name_limit_fail, Toast.LENGTH_SHORT).show()
    }

    private fun passwordVerification(password: String, confirmPassword: String): Boolean{
        return password.equals(confirmPassword) && password.length > 6
    }

    private fun addUserToDB(user: FirebaseUser){
        val userRef = mDatabase!!.child("Users")
        userRef.child(user.uid).child("email").setValue(mEmail)
        userRef.child(user.uid).child("name").setValue(mName)
    }

}