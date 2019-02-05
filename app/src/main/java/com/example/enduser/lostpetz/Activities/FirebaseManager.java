package com.example.enduser.lostpetz.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {
    /*
    This is a singleton class that will hold a reference to Firebase related variables
     */

    private static FirebaseManager firebaseInstance = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mUser;

    private FirebaseManager(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public static FirebaseManager getInstance(){
        if(firebaseInstance == null){
            synchronized (FirebaseManager.class){
                if(firebaseInstance == null){
                    firebaseInstance = new FirebaseManager();
                }
            }
        }
        return firebaseInstance;
    }

    public FirebaseUser getUser(){
        return mUser;
    }

    public void signInViaAuth(String email, String password, final Context context){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mUser = mAuth.getCurrentUser();
                    //addUserToDb();
                }
                setBroadcastReceiver(task.isSuccessful(),context);
            }
        });
    }
    //Used as a callback to the user's successful or unsuccessful sign in
    private void setBroadcastReceiver(boolean isSuccessful, Context context){
        Intent broadcastIntent = new Intent("yeet").putExtra("value", isSuccessful);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
    }

    //TODO --> investigate how necessary this is
    private void addUserToDb(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        Log.e("user added", "to db");
        DatabaseReference userRef = mDatabaseRef.child(mUser.getUid());
        userRef.child("email").setValue(mUser.getEmail());
        if(mUser.getDisplayName() != null){
            userRef.child("name").setValue(mUser.getDisplayName());
        }

        /*
        og code for this
        Check is the user's uid is in the user's sharedPreferences, if it's not then it adds it to the DB and
        shared preference

        private fun addUserToDB(){
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val key = getString(R.string.user_name_shared_pref)
            if(!(sharedPref.getString(key, "null").equals(mUser!!.uid))) {
                with(sharedPref.edit()) {
                    putString(getString(R.string.user_name_shared_pref), mUser?.uid)
                    apply()
                }
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users")

                val userRef = mDatabaseRef!!.child(mUser!!.uid)
                userRef.child("email").setValue(mUser!!.email)
                if(mUser!!.displayName != null) {
                    userRef.child("name").setValue(mUser!!.displayName)
                }
            }
    }
         */
    }
}
