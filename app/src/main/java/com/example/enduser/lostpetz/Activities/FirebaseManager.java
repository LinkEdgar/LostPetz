package com.example.enduser.lostpetz.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.enduser.lostpetz.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

    //Google sign in
    private GoogleSignInClient mGoogleSignInClient;

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
                setBroadcastReceiver("auth",task.isSuccessful(),context);
            }
        });
    }
    //Used as a callback to the user's successful or unsuccessful sign in
    private void setBroadcastReceiver(String key, boolean isSuccessful, Context context){
        Intent broadcastIntent = new Intent("SignIn").putExtra("value", isSuccessful).putExtra("key",key);
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

    public void passwordRecovery(String email, final Context context){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setBroadcastReceiver("pass_reset", task.isSuccessful(), context);
            }
        });
    }

    public void signInViaGoogle(GoogleSignInAccount account, final Context context){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setBroadcastReceiver("auth_google",task.isSuccessful(),context);
            }
        });
    }

    public void enableGoogleSignIn(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public GoogleSignInClient getmGoogleSignInClient() {
        return mGoogleSignInClient;
    }
}
