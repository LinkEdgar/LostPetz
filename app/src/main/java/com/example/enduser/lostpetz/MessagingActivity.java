package com.example.enduser.lostpetz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class MessagingActivity extends AppCompatActivity {


    //Firebase instance variables
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener mChildListener;
    private ValueEventListener mValueEventListener;

    //Test textview to connect with the database
    /* TODO replace testTextView with pet summer layout */
    private TextView testTextView;
    private EditText mUserTextInput;

    //hashset used to eliminate double messages
    private HashSet<String> messageHashset;

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
        mRecylerView.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(this);

        mRecylerView.setLayoutManager(mLayoutManager);

        messageHashset = new HashSet<>();

        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String snapshotKey = dataSnapshot.getKey();
                if(!messageHashset.contains(snapshotKey)) {
                    messageHashset.add(snapshotKey);
                    Message receivedMessage = dataSnapshot.getValue(Message.class);
                    Log.e("Message Test", " " + receivedMessage.getMessage() + "  " + dataSnapshot);
                    //textTextView.setText(receivedMessage.getMessage());

                    mMessageArray.add(receivedMessage);
                    mAdapter.notifyDataSetChanged();
                }
                else{
                    //FOR TESTING ONLY
                    Log.e("hashset", "Already in the set");
                }
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

        /*
        Not sure --> if it will be efficient to load all of the children as that may be too much for the app
         */
        mRef.addChildEventListener(mChildListener);
    }

    public void sendMessage(View view){
        //TODO eventually change the null values
        String textToSend = mUserTextInput.getText().toString().trim();
        if(textToSend != null && textToSend.length() > 0){
            Message messageToSend = new Message("Anonymous", textToSend, null, null);
            mRef.push().setValue(messageToSend);
            mUserTextInput.setText("");
            mRecylerView.smoothScrollToPosition(mMessageArray.size());

        }
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
