package com.example.enduser.lostpetz;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
 */
public class MessagingActivity extends AppCompatActivity {


    //Firebase instance variables
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener mChildListener;
    private FirebaseStorage mStorage;

    //Test textview to connect with the database
    /* TODO replace testTextView with pet summary layout */
    private TextView testTextView;
    private EditText mUserTextInput;

    //hashset used to eliminate double messages
    private HashSet<String> messageHashset;

    //Message console
    private Uri mImageUri;
    private ImageView mImageToSend;
    private EditText mTextToSend;
    private ImageButton mImageCanceButton;
    private boolean isPictureMessage = false;
    private ProgressBar mUploadProgressbar;

    //RecyclerView
    private ArrayList<Message> mMessageArray;
    private RecyclerView mRecylerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private final int RC_PICK_IMAGE = 3141;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        //get Firebase instance
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child(FirebaseValues.FirebaseDatabaseValues.FIREBASE_MESSAGES_ROOT);
        mStorage = FirebaseStorage.getInstance();


        //View Referencing
        testTextView = findViewById(R.id.test_display_textview);
        mUserTextInput = findViewById(R.id.messenger_user_input_text);
        mTextToSend = findViewById(R.id.messenger_user_input_text);
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

        mRecylerView.setAdapter(mAdapter);
        mRecylerView.setHasFixedSize(true);

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
                    mMessageArray.add(receivedMessage);
                    mAdapter.notifyItemInserted(mMessageArray.size());
                    mRecylerView.smoothScrollToPosition(mMessageArray.size());
                    Log.e("Hashset contetns", " "+ messageHashset);
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
        mRef.orderByKey().addChildEventListener(mChildListener);
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
            if (textToSend != null && textToSend.length() > 0) {
                Message messageToSend = new Message("Anonymous", textToSend, null, null);
                mRef.push().setValue(messageToSend);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PICK_IMAGE && resultCode == RESULT_OK
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
        mTextToSend.setVisibility(View.GONE);
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
    protected void onPause() {
        super.onPause();
        mRef.removeEventListener(mChildListener);
    }

    /*
    Overridden to remove listeners
     */
    @Override
    protected void onResume() {
        super.onResume();
        mRef.addChildEventListener(mChildListener);
    }
    /*
    Uploads the user selected image to to storage and returns the string
    for the download url
     */
    private void uploadSelectedImage(Uri imageUri){
        Uri file = imageUri;
        UploadTask uploadTask = mStorage.getReference("Images/"+ imageUri.getLastPathSegment()).putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Upload Picture Image", "could not properly upload image");
                //TODO add message to resources
                Toast.makeText(MessagingActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
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
                "Username",
                null,
                pictureUrl,
                null);
        mRef.push().setValue(pictureMessageToSend);
        mRecylerView.smoothScrollToPosition(mMessageArray.size());
    }
}
