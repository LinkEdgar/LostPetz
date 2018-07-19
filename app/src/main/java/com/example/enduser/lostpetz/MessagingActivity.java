package com.example.enduser.lostpetz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashSet;

/*
    The message console refers to the bottom layout that the user interacts with to
    send pictures. This includes the two image buttons, gallery and send, as well as the
    corresponding UI.
    The application handles full screen pictures via a custom dialog fragment that will
    get picture information such as the url from a android preferences
 */
public class MessagingActivity extends AppCompatActivity implements MessageAdapter.onPitureClicked{


    //Firebase instance variables
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener mChildListener;
    private ValueEventListener mValueListener;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private DatabaseReference mChatRef;
    //Test textview to connect with the database
    /* TODO replace testTextView with pet summary layout */
    private TextView testTextView;
    private EditText mUserTextInput;
    private TextView mNoMessagesTextView;

    //hashset used to eliminate double messages
    private HashSet<String> messageHashset;

    //Message console
    private Uri mImageUri;
    private ImageView mImageToSend;
    private ImageButton mImageCanceButton;
    private boolean isPictureMessage = false;
    private ProgressBar mUploadProgressbar;

    //RecyclerView
    private ArrayList<Message> mMessageArray;
    private RecyclerView mRecylerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /*

     */
    //message key creation
    private String posterId;
    private String currentUserId;
    private String jointUserChat;
    private String INTENT_GET_POSTER_ID_KEY = "posterId";

    private final int RC_PICK_IMAGE = 3141;

    //TODO add a query check so that not all of the messages are loaded at once


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        handleIntentData();
        initFirebase();
        initUi();

    }

    private void initUi(){
        //View Referencing
        mNoMessagesTextView = findViewById(R.id.messenger_activity_no_messages_tv);
        testTextView = findViewById(R.id.test_display_textview);
        mUserTextInput = findViewById(R.id.messenger_user_input_text);
        mImageToSend = findViewById(R.id.messenger_image_to_send);
        mUploadProgressbar = findViewById(R.id.messenger_upload_progressbar);
        mImageCanceButton = findViewById(R.id.messenger_cancel_image_selection);
        mImageCanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelImageSelection();
            }
        });

        mRecylerView = findViewById(R.id.messaging_recycler_view);

        mMessageArray = new ArrayList<>();

        mAdapter = new MessageAdapter(mMessageArray);
        mAdapter.setOnClick(this);

        mRecylerView.setAdapter(mAdapter);
        mRecylerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(MessagingActivity.this);

        mRecylerView.setLayoutManager(mLayoutManager);

        messageHashset = new HashSet<>();

        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String snapshotKey = dataSnapshot.getKey();
                if(!messageHashset.contains(snapshotKey)) {
                    messageHashset.add(snapshotKey);
                    Message receivedMessage = dataSnapshot.getValue(Message.class);
                    mMessageArray.add(receivedMessage);
                    mAdapter.notifyItemInserted(mMessageArray.size());
                    mRecylerView.smoothScrollToPosition(mMessageArray.size());
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

        mValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                This method check if to see if there are any messages and sets the UI accordingly
                 */
                checkIfFirstMessage();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Syncs data after childevent listener
        mChatRef.addListenerForSingleValueEvent(mValueListener);

        mChatRef.limitToLast(1).orderByKey().addChildEventListener(mChildListener);
    }

    private void initFirebase(){
        //get Firebase instance
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child(FirebaseValues.FirebaseDatabaseValues.FIREBASE_MESSAGES_ROOT);
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid().toString();
        mChatRef = mRef.child(jointUserChat);
    }

    //If the user chat is empty a chat value must be set for both users (poster and current user)
    private void associateChatWithUsers(){
        //sets chat for the poster
        DatabaseReference chatRef = mDatabase.getReference("Users").child(posterId).child("chats");
        DatabaseReference specificRef = chatRef.push();
        specificRef.setValue(jointUserChat);
        //sets chat for current user
        chatRef = mDatabase.getReference("Users").child(currentUserId).child("chats");
        specificRef = chatRef.push();
        specificRef.setValue(jointUserChat);


    }

    /*
    Depending on whether the message is text based or picture based this method will appropriately
    add it to the database and display it in the chat application. Sets the appropriate views
    depending on the type of message
     */
    public void sendMessage(View view){
        if(!isPictureMessage) {
            //TODO eventually change the null values
            String textToSend = mUserTextInput.getText().toString().trim();
            if (textToSend.length() > 0) {
                mNoMessagesTextView.setVisibility(View.GONE);
                Message messageToSend = new Message("Anonymous", textToSend, null, null);
                mChatRef.push().setValue(messageToSend);
                mUserTextInput.setText("");
                mRecylerView.smoothScrollToPosition(mMessageArray.size());

            }
        }
        else{
            mUploadProgressbar.setVisibility(View.VISIBLE);
            mImageToSend.setVisibility(View.GONE);
            mImageCanceButton.setVisibility(View.GONE);
            uploadSelectedImage(mImageUri);
        }
    }

    /*
    starts an intent to select an image from the user's gallery
     */
    public void selectImageFromGallery(View view){
        Intent selectImageIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(selectImageIntent, RC_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PICK_IMAGE && resultCode == Activity.RESULT_OK
                && null != data){
            mImageUri = data.getData();
            setImageToConsole(mImageUri);

        }
    }


    /*
    Appropriately hides and views so that the user selected image displays properly.
    It takes in a the Uri from the intent to set a preview of their selection in the
    message console
     */
    public void setImageToConsole(Uri uri){
        mUserTextInput.setVisibility(View.GONE);
        mImageToSend.setVisibility(View.VISIBLE);
        mImageToSend.setImageResource(R.mipmap.ic_launcher);
        mImageCanceButton.setVisibility(View.VISIBLE);
        Glide.with(this).load(uri).into(mImageToSend);
        isPictureMessage = true;
    }
    /*
    Called when the cancel image button and it nulls the image uri and sets the picture related views
    to gone. It set the user input edit text back to visible and nulls the uri. Lastly, the
    isPicture boolean is changed to false.
     */
    public void cancelImageSelection(){
        mUserTextInput.setVisibility(View.VISIBLE);
        mImageCanceButton.setVisibility(View.GONE);
        mImageToSend.setVisibility(View.GONE);
        isPictureMessage = false;
        mImageUri = null;
    }

    /*
    Overridden to remove listeners
     */
    @Override
    public void onPause() {
        super.onPause();
        mChatRef.removeEventListener(mChildListener);
    }



    /*
    Overridden to remove listeners
     */
    @Override
    public void onResume() {
        super.onResume();
        mChatRef.limitToLast(1).addChildEventListener(mChildListener);
    }

    /*
    Uploads the user selected image to to storage and returns the string
    for the download url
     */
    private void uploadSelectedImage(Uri imageUri){
        UploadTask uploadTask = mStorage.getReference("Images/"+ imageUri.getLastPathSegment()).putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Upload Picture Image", "could not properly upload image");
                Toast.makeText(MessagingActivity.this,
                        getResources().getString(R.string.image_upload_failed)
                        , Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                mUploadProgressbar.setVisibility(View.GONE);
                cancelImageSelection();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sendPictureMessage(taskSnapshot.getDownloadUrl().toString());
                Toast.makeText(MessagingActivity.this, "Image upload successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    This method take an image url and creates a message object to send to the realtime database.
    It then scrolls the recyclerview to the bottom of the list
     */
    private void sendPictureMessage(String pictureUrl){
        //TODO actually populate message with real data
        Message pictureMessageToSend = new Message(
                "Username",
                null,
                pictureUrl,
                null);
        mChatRef.push().setValue(pictureMessageToSend);
        mRecylerView.smoothScrollToPosition(mMessageArray.size());
    }

    /*
    This method will display a fullscreen version of the image sent in
    the messenger
     */

    @Override
    public void onPictureClicked(int position) {
        FullScreenDialog dialogFragment = new FullScreenDialog();
        String url = mMessageArray.get(position).getPhotoUrl();
        dialogFragment.setImageUrl(url);
        dialogFragment.show(getSupportFragmentManager(),"Fragment");
    }

    /*
    Gets the poster's uId in order to create a unique chat
     */
    private void handleIntentData(){
        posterId = getIntent().getStringExtra(INTENT_GET_POSTER_ID_KEY);
        jointUserChat = currentUserId+posterId;
    }

    /*
    This  method will set the UI to display a text message if there are no messages otherwise
    it will set that text view to gone
     */
    private void checkIfFirstMessage(){
        if(mMessageArray.size() == 0){
            associateChatWithUsers();
            mNoMessagesTextView.setVisibility(View.VISIBLE);
        }else mNoMessagesTextView.setVisibility(View.GONE);
    }
}
