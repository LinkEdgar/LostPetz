package com.example.enduser.lostpetz.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.enduser.lostpetz.Adapters.MessageAdapter;
import com.example.enduser.lostpetz.FirebaseValues;
import com.example.enduser.lostpetz.FullScreenDialog;
import com.example.enduser.lostpetz.CustomObjectClasses.Message;
import com.example.enduser.lostpetz.R;
import com.example.enduser.lostpetz.CustomObjectClasses.User;
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
public class MessagingActivity extends AppCompatActivity implements MessageAdapter.onPitureClicked, SwipeRefreshLayout.OnRefreshListener{


    //Firebase variables
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener mChildListener;
    private ValueEventListener mValueListener;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private DatabaseReference mChatRef;
    private EditText mUserTextInput;
    private TextView mNoMessagesTextView;
    private ProgressBar mRefreshProgressBar;

    //hashset used to eliminate double messages
    private HashSet<String> messageHashset;

    //Message console
    private Uri mImageUri;
    private ImageView mImageToSend;
    private ImageButton mImageCanceButton;
    private boolean isPictureMessage = false;
    private ProgressBar mUploadProgressbar;

    //RecyclerView
    private SwipeRefreshLayout mRefreshLayout;
    private ArrayList<Message> mMessageArray;
    private RecyclerView mRecylerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /*
    Chats are created by concatenating the user's uid and that becomes the ID for the new chat between users
     */
    //message key creation
    private String posterId;// user that posted the pet
    private String currentUserId;
    private String jointUserChat;
    private String INTENT_GET_POSTER_ID_KEY = "posterId";
    private String INTENT_GET_JOINT_CHAT_KEY = "jointChat";

    //TODO bundle user
    private User mUser;

    private int messageQuantity = 12;

    private boolean isRefresh = false;

    private final int RC_PICK_IMAGE = 3141;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        handleIntentData();
        initFirebase();
        getUserData();
        initUi();

    }

    private void initUi(){
        //View Referencing
        mRefreshLayout = findViewById(R.id.messaging_swipe_layout);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshProgressBar = findViewById(R.id.messenger_refresh_progressbar);
        mNoMessagesTextView = findViewById(R.id.messenger_activity_no_messages_tv);
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
        mUser = new User(null, null, null, null, null);

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
                addMessageToAdapter(dataSnapshot);
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
                checkIfMessagesEmpty();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Syncs data after childevent listener
        mChatRef.addListenerForSingleValueEvent(mValueListener);

        mChatRef.limitToLast(messageQuantity).orderByKey().addChildEventListener(mChildListener);
    }

    private void initFirebase(){
        //get Firebase instance
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child(FirebaseValues.FirebaseDatabaseValues.FIREBASE_MESSAGES_ROOT);
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid().toString();
        if(jointUserChat == null) { //checks if it was previously assigned via handleIntent()
            jointUserChat = currentUserId + posterId;
        }

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
            String textToSend = mUserTextInput.getText().toString().trim();
            if (textToSend.length() > 0) {
                mNoMessagesTextView.setVisibility(View.GONE);
                Message messageToSend = new Message(mUser.getUserName(), textToSend, null, mUser.getProfileUrl());
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
        mChatRef.limitToLast(messageQuantity).orderByKey().addChildEventListener(mChildListener);
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
        Message pictureMessageToSend = new Message(
                mUser.getUserName(),
                null,
                pictureUrl,
                mUser.getProfileUrl());
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
        Intent intent = getIntent();
        if(intent.getStringExtra(INTENT_GET_POSTER_ID_KEY) != null) {
            posterId = getIntent().getStringExtra(INTENT_GET_POSTER_ID_KEY);
        }else{
            jointUserChat = intent.getStringExtra(INTENT_GET_JOINT_CHAT_KEY);
        }
    }

    /*
    This  method will set the UI to display a text message if there are no messages otherwise
    it will set that text view to gone
     */
    private void checkIfMessagesEmpty(){
        if(mMessageArray.size() == 0){
            associateChatWithUsers();
            mNoMessagesTextView.setVisibility(View.VISIBLE);
        }else mNoMessagesTextView.setVisibility(View.GONE);
    }

    //Check the DB based on the user's UID
    private void getUserData(){
        DatabaseReference mUserRef = mDatabase.getReference("Users").child(currentUserId);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUserData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Takes a snapshot and extracts info to add to our User object
    private void setUserData(DataSnapshot snapshot){
        mUser.setUserName(snapshot.child("name").getValue(String.class));
        mUser.setProfileUrl(snapshot.child("profileUrl").getValue(String.class));
        mUser.setEmail(snapshot.child("email").getValue(String.class));
    }

    /*
    Adds messages from datasnapshot to adapter, if the this is a refresh from the user scrolling up then the
    messages are written added to the top of the list as to maintain the order of the messages
     */

    private void addMessageToAdapter(DataSnapshot dataSnapshot){
        String snapshotKey = dataSnapshot.getKey();
            if (!messageHashset.contains(snapshotKey)) {
                messageHashset.add(snapshotKey);
                Message receivedMessage = dataSnapshot.getValue(Message.class);
                mMessageArray.add(receivedMessage);
                mAdapter.notifyItemInserted(mMessageArray.size());
                mRecylerView.smoothScrollToPosition(mMessageArray.size());// scrolls to the bottom
            }
    }

    private void refreshScroll(DataSnapshot snapshot){

        int count = 0;
        Log.e("refresh snap", ""+ snapshot);
        for(DataSnapshot snapshot1 : snapshot.getChildren()){
            String key = snapshot1.getKey();
            if(!messageHashset.contains(key)) {
                messageHashset.add(key);
                String message = snapshot1.child("message").getValue(String.class);
                String userName = snapshot1.child("userName").getValue(String.class);
                String photoUrl = snapshot1.child("photoUrl").getValue(String.class);
                Message messageToAdd = new Message();
                messageToAdd.setMessage(message);
                messageToAdd.setPhotoUrl(photoUrl);
                messageToAdd.setUserName(userName);
                mMessageArray.add(count,messageToAdd);
                mAdapter.notifyItemInserted(count);
                count++;
            }
        }
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        messageQuantity = messageQuantity +8;
        mChatRef.limitToLast(messageQuantity).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refreshScroll(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
