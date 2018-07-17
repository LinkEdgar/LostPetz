package com.example.enduser.lostpetz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddPetFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, ChooseDateDialogFragment.onClicked{
    private Spinner mDateSpinner;
    private TextInputEditText mPetNameEditText;
    private TextInputEditText mPetZipEditText;
    private TextInputEditText mPetDescriptionEditText;
    private ImageView mImageToUpload;
    private ImageView mImageToUpload2;
    private ImageView mImageToUpload3;
    private ImageView mImageCancel;
    private ImageView mImageCancelTwo;
    private ImageView mImageCancelThree;
    private ChooseDateDialogFragment chooseDateDialogFragment;
    private ArrayAdapter<String> adapter;
    private List<String> mSpinnerList;
    private Button mSubmitButton;
    private Pet petToAdd;
    private static String PET_BUNDLE_KEY = "pet";
    private static String IMAGE_COUNTER_KEY = "imageCounter";
    private static String IMAGE_URI_ONE_KEY = "uri1";
    private static String IMAGE_URI_TWO_KEY = "uri2";
    private static String IMAGE_URI_THREE_KEY = "uri3";

    private int imageCounter = 0;

    //firebase
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;


    private Uri[] imageUriArray; //used to hold the Uri's that will be uploaded
    private static int DEFAULT_IMAGE_SELECT_RESOURCE = R.mipmap.ic_launcher_round;

    private int imageSelectorPosition; //used to keep track of the uri the user is choosing

    private static final int IMAGE_GALLERY_RESULT = 3141;

    //Pet detail variables
    private String dateLost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_pet, container, false);
        //firebase storage
        mStorage = FirebaseStorage.getInstance();
        initUI(rootView);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_pet_upload_one:
                startGalleryIntent(0);
                break;
            case R.id.add_pet_upload_two:
                startGalleryIntent(1);
                break;
            case R.id.add_pet_upload_three:
                startGalleryIntent(2);
                break;
            case R.id.add_pet_cancel_image_one:
                removeImageFromList(0);
                break;
            case R.id.add_pet_cancel_image_two:
                removeImageFromList(1);
                break;
            case R.id.add_pet_cancel_image_three:
                removeImageFromList(2);
                break;
            case R.id.add_pet_submit_button:
                submitPet();
                break;

        }

    }
    /*
    Initializes views and sets on click listeners
     */
    private void initUI(View rootView){
        mSpinnerList = new ArrayList<>();
        mSpinnerList.add("Today");
        mSpinnerList.add("Set Date");
        mDateSpinner = rootView.findViewById(R.id.add_pet_date_spinner);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, mSpinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDateSpinner.setAdapter(adapter);
        mDateSpinner.setOnItemSelectedListener(this);
        mImageToUpload3 = rootView.findViewById(R.id.add_pet_upload_three);
        mImageToUpload3.setOnClickListener(this);
        mImageToUpload = rootView.findViewById(R.id.add_pet_upload_one);
        mImageToUpload.setOnClickListener(this);
        mImageToUpload2 = rootView.findViewById(R.id.add_pet_upload_two);
        mImageToUpload2.setOnClickListener(this);
        mImageToUpload3.setOnClickListener(this);
        mPetNameEditText = rootView.findViewById(R.id.add_pet_name);
        mPetZipEditText = rootView.findViewById(R.id.add_pet_zip);
        mPetDescriptionEditText = rootView.findViewById(R.id.add_pet_description);
        mImageCancel = rootView.findViewById(R.id.add_pet_cancel_image_one);
        mImageCancel.setOnClickListener(this);
        mImageCancelTwo = rootView.findViewById(R.id.add_pet_cancel_image_two);
        mImageCancelTwo.setOnClickListener(this);
        mImageCancelThree = rootView.findViewById(R.id.add_pet_cancel_image_three);
        mImageCancelThree.setOnClickListener(this);
        imageUriArray = new Uri[3];
        mSubmitButton = rootView.findViewById(R.id.add_pet_submit_button);
        mSubmitButton.setOnClickListener(this);
        petToAdd = new Pet();

    }

    /*
    Creates an instance of a chooseDialogFragment and sets its onClick listener
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int selectPosition = mDateSpinner.getSelectedItemPosition();
        if(selectPosition == 1 ) {
            chooseDateDialogFragment = new ChooseDateDialogFragment();
            chooseDateDialogFragment.setOnClicked(this);
            chooseDateDialogFragment.show(getActivity().getFragmentManager(), "chooseDate");
        } else{
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
            dateLost = formatter.format(date);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    /*
    This method is called when the user clicks done in the ChooseDateDialogFragment.
    The boolean is used to check whether the user actually chose a date or just cancelled.
    We reset the list position if the user does not select anything. The list will maintain a max length
    of 3, "today", "set date," and the most recent date selection
     */

    @Override
    public void onClicked(boolean didUserSelectDate) {
        dateLost = chooseDateDialogFragment.getDateFromPicker();
        if(!didUserSelectDate) {
            mDateSpinner.setSelection(0);
        }else{
            if(mSpinnerList.size() > 2){
                mSpinnerList.remove(2);
            }
            mSpinnerList.add(dateLost);
            adapter.notifyDataSetChanged();
            mDateSpinner.setSelection(2);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_GALLERY_RESULT && resultCode == Activity.RESULT_OK && data != null){
            Uri uri = data.getData();
            addImageToList(uri,imageSelectorPosition, false);
        }
    }

    /*
    Sets views based on the size of imageUriList. Currently there is only support for a maximum of
    three images per pet entry into the DB. The third parameter determines whether or not to increase the
    imageCounter variable based on whether the call is being made from onRestoreInstanceState or not. This
    ensures that the imageCounter reflects correct amount of images
     */
    private void addImageToList(Uri uri, int position, boolean isCallFromInstanceState){
        if(!isCallFromInstanceState)imageCounter++;
        imageUriArray[position] = uri;
        switch(position){
            case 0:
                Glide.with(this).load(uri).into(mImageToUpload);
                mImageCancel.setVisibility(View.VISIBLE);
                break;
            case 1:
                Glide.with(this).load(uri).into(mImageToUpload2);
                mImageCancelTwo.setVisibility(View.VISIBLE);
                break;
            case 2:
                Glide.with(this).load(uri).into(mImageToUpload3);
                mImageCancelThree.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void removeImageFromList(int position){
        imageCounter--;
            imageUriArray[position] = null;
            switch(position){
                case 0:
                    mImageToUpload.setImageResource(DEFAULT_IMAGE_SELECT_RESOURCE);
                    mImageCancel.setVisibility(View.GONE);
                    break;
                case 1:
                    mImageToUpload2.setImageResource(DEFAULT_IMAGE_SELECT_RESOURCE);
                    mImageCancelTwo.setVisibility(View.GONE);
                    break;
                case 2:
                    mImageToUpload3.setImageResource(DEFAULT_IMAGE_SELECT_RESOURCE);
                    mImageCancelThree.setVisibility(View.GONE);
                    break;
            }
    }

    private void startGalleryIntent(int position){
        Intent searchGalleryIntent = new Intent();
        searchGalleryIntent.setType("image/*");
        imageSelectorPosition = position; //sets the position for onActivityResult
        searchGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(searchGalleryIntent,"Select Picture"), IMAGE_GALLERY_RESULT);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PET_BUNDLE_KEY, petToAdd);
        Log.e("onSave", " "+ imageCounter);
        outState.putInt(IMAGE_COUNTER_KEY, imageCounter);
        if(imageUriArray[0] != null) outState.putParcelable(IMAGE_URI_ONE_KEY, imageUriArray[0]);
        if(imageUriArray[1]!= null) outState.putParcelable(IMAGE_URI_TWO_KEY, imageUriArray[1]);
        if(imageUriArray[2]!= null) outState.putParcelable(IMAGE_URI_THREE_KEY, imageUriArray[2]);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey(PET_BUNDLE_KEY)) {
                petToAdd = savedInstanceState.getParcelable(PET_BUNDLE_KEY);
            }
            if(savedInstanceState.containsKey(IMAGE_COUNTER_KEY)){
                Log.e("image bundle", " "+ savedInstanceState.getInt(IMAGE_COUNTER_KEY));
                imageCounter = savedInstanceState.getInt(IMAGE_COUNTER_KEY, 0);
                Log.e("imageCounter", ""+ imageCounter);
            }else Log.e("imageCOunter", "Not in bouble"+ imageCounter);

            if(savedInstanceState.containsKey(IMAGE_URI_ONE_KEY)){
                addImageToList((Uri)savedInstanceState.getParcelable(IMAGE_URI_ONE_KEY),0,true);
            }
            if(savedInstanceState.containsKey(IMAGE_URI_TWO_KEY)){
                addImageToList((Uri)savedInstanceState.getParcelable(IMAGE_URI_TWO_KEY),1,true);
            }
            if(savedInstanceState.containsKey(IMAGE_URI_THREE_KEY)){
                addImageToList((Uri)savedInstanceState.getParcelable(IMAGE_URI_THREE_KEY),2,true);
            }
        }
    }

    private void submitPet(){
        if(checkNullRequirements()){
            petToAdd.setName(mPetNameEditText.getText().toString().trim());
            petToAdd.setZip(mPetZipEditText.getText().toString().trim());
            petToAdd.setDescription(mPetDescriptionEditText.getText().toString().trim());
            petToAdd.setDateLost(dateLost);
            initiatePictureUpload();
        }
    }

    private boolean checkNullRequirements(){

        return true;
    }

    private void addPetToFirebase(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference specificRef = mDatabase.child("Pets").push();
        specificRef.setValue(petToAdd);
        Toast.makeText(getContext(), "Pet added", Toast.LENGTH_SHORT).show();
    }

    /*
    Clears all the UI from any former data that might have been present, it also resets image selections
    and the URI array 
     */
    private void clearUI(){
        petToAdd = new Pet();
        Log.e("Pet test", "->"+ petToAdd.getName());
        mPetNameEditText.setText("");
        mPetDescriptionEditText.setText("");
        mPetZipEditText.setText("");
        for(int x = 0; x< imageUriArray.length; x++){
            imageUriArray[x] = null;
        }
        mImageToUpload.setImageResource(DEFAULT_IMAGE_SELECT_RESOURCE);
        mImageCancel.setVisibility(View.GONE);
        mImageToUpload2.setImageResource(DEFAULT_IMAGE_SELECT_RESOURCE);
        mImageCancelTwo.setVisibility(View.GONE);
        mImageToUpload3.setImageResource(DEFAULT_IMAGE_SELECT_RESOURCE);
        mImageCancelThree.setVisibility(View.GONE);
        dateLost = null;
        imageCounter = 0;
        if(mSpinnerList.size() >2)mSpinnerList.remove(2);

        mDateSpinner.setSelection(0);
    }

    private void uploadPictures(){
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Uploading ...");
        int counter = 0;// counts the amount of uploaded pictures
        for(int x = 0; x < imageUriArray.length; x++){
            if(imageUriArray[x] != null){
                counter++;
                dialog.show();
                if(counter == imageCounter) {

                    UploadTask uploadTask = mStorage.getReference("petImages/"+ imageUriArray[x].getLastPathSegment()).putFile(imageUriArray[x]);

                    final int finalCounter = counter;
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Upload Picture Image", "could not properly upload image");
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            addPetToFirebase();
                            clearUI();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            setImageUrl(taskSnapshot.getDownloadUrl().toString(), finalCounter);
                        }
                    });

                }else {
                    dialog.show();
                    UploadTask uploadTask = mStorage.getReference("petImages/"+imageUriArray[x].getLastPathSegment()).putFile(imageUriArray[x]);
                    final int finalCounter1 = counter;
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            setImageUrl(taskSnapshot.getDownloadUrl().toString(), finalCounter1);
                        }
                    });
                }
            }
        }
    }

    private void initiatePictureUpload(){
        if(imageCounter > 0){
            uploadPictures();
        }else{
            addPetToFirebase();
            clearUI();
        }
    }

    //This method takes the download url from a snapshot and adds it to the pet based on its position
    private void setImageUrl(String url, int postition){
        switch(postition){
            case 1:
                petToAdd.setProfileUrlOne(url);
                break;
            case 2:
                petToAdd.setProfileUrlTwo(url);
                break;
            case 3:
                petToAdd.setProfileUrlThree(url);
                break;
        }
    }
}
