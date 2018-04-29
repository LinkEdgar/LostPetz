package com.example.enduser.lostpetz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessagingActivity extends AppCompatActivity {


    //Firebase instance variables
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener mChildListener;

    //Test textview to connect with the database
    /* TODO replace testTextView with pet summer layout */
    private TextView testTextView;
    private EditText mUserTextInput;

    //RecyclerView
    private ArrayList<Message> mMessageArray;
    private RecyclerView mRecylerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        //get Firebase instance
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Messages");


        //View Referencing
        testTextView = findViewById(R.id.test_display_textview);
        mUserTextInput = findViewById(R.id.messenger_user_input_text);

        mRecylerView = findViewById(R.id.messaging_recycler_view);

        mMessageArray = new ArrayList<>();

        mAdapter = new MessageAdapter(mMessageArray);

        mRecylerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);

        mRecylerView.setLayoutManager(mLayoutManager);





        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Message receivedMessage = dataSnapshot.getValue(Message.class);
                Log.e("Message Test"," "+ receivedMessage.getMessage()+"  "+ dataSnapshot);
                //textTextView.setText(receivedMessage.getMessage());

                mMessageArray.add(receivedMessage);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //TODO --> test queries and how to only get the last x amount
        mRef.limitToLast(3).addChildEventListener(mChildListener);





    }
    /*TODO --> Replace with the real send message method*/
    public void testMessageClass(View view){
        String textToSend = mUserTextInput.getText().toString().trim();
        Message messageToSend = new Message("Anonymous",textToSend,null,null);
        if(textToSend != null){
            mRef.push().setValue(messageToSend);
        }
        mUserTextInput.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRef.removeEventListener(mChildListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRef.addChildEventListener(mChildListener);
    }
}
