package com.example.enduser.lostpetz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessagingActivity extends AppCompatActivity {


    //Firebase instance variables
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    //Test textview to connect with the database
    private TextView textTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        //get Firebase instance
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Messages");

        //View Referencing
        textTextView = findViewById(R.id.test_display_textview);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textTextView.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
