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
public class MessagingActivity extends Fragment implements MessageAdapter.onPitureClicked{


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
    private ImageButton mImageCanceButton;
    private boolean isPictureMessage = false;
    private ProgressBar mUploadProgressbar;

    //RecyclerView
    private ArrayList<Message> mMessageArray;
    private RecyclerView mRecylerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private final int RC_PICK_IMAGE = 3141;

    //TODO add a query check so that not all of the messages are loaded at once


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_messaging, container, false);
        initUi(rootView);
        return rootView;
    }

    private void initUi(View rootView){

        //get Firebase instance
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child(FirebaseValues.FirebaseDatabaseValues.FIREBASE_MESSAGES_ROOT);
        mStorage = FirebaseStorage.getInstance();

        //View Referencing
        testTextView = rootView.findViewById(R.id.test_display_textview);
        mUserTextInput = rootView.findViewById(R.id.messenger_user_input_text);
        mImageToSend = rootView.findViewById(R.id.messenger_image_to_send);
        mUploadProgressbar = rootView.findViewById(R.id.messenger_upload_progressbar);
        mImageCanceButton = rootView.findViewById(R.id.messenger_cancel_image_selection);
        mImageCanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelImageSelection();
            }
        });

        mRecylerView = rootView.findViewById(R.id.messaging_recycler_view);

        mMessageArray = new ArrayList<>();

        mAdapter = new MessageAdapter(mMessageArray);
        mAdapter.setOnClick(this);

        mRecylerView.setAdapter(mAdapter);
        mRecylerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());

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
            if (textToSend.length() > 0) {
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
        mRef.removeEventListener(mChildListener);
    }



    /*
    Overridden to remove listeners
     */
    @Override
    public void onResume() {
        super.onResume();
        mRef.addChildEventListener(mChildListener);
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
                Toast.makeText(getContext(),
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
                Toast.makeText(getContext(), "Image upload successful", Toast.LENGTH_SHORT).show();
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
        mRef.push().setValue(pictureMessageToSend);
        mRecylerView.smoothScrollToPosition(mMessageArray.size());
    }
    /*
    This method will display a fullscreen version of the image sent in
    the messenger
     */

    @Override
    public void onPictureClicked(int position) {
        //TODO pass url information for full screen
        FullScreenDialog dialogFragment = new FullScreenDialog();
        String url = mMessageArray.get(position).getPhotoUrl();
        dialogFragment.setImageUrl(url);
        dialogFragment.show(getFragmentManager(),"Fragment");
    }
}
