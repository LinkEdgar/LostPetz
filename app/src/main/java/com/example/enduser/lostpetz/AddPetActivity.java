package com.example.enduser.lostpetz;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AddPetActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ChooseDateDialogFragment.onClicked{
    private ArrayList<Pet> mArrayList;
    private Spinner mDateSpinner;
    private TextInputEditText mPetNameEditText;
    private TextInputEditText mPetZipEditText;
    private TextInputEditText mPetDescriptionEditText;
    private Button mSelectImageButton;
    private ImageView mImageToUpload;
    private ImageView mImageToUpload2;
    private ImageView mImageToUpload3;
    private ImageView mImageCancel;
    private ImageView mImageCancelTwo;
    private ImageView mImageCancelThree;
    private ChooseDateDialogFragment chooseDateDialogFragment;
    private ArrayAdapter<String> adapter;
    private List<String> mSpinnerList;

    private LinkedList<Uri> imageUriList; //used to hold the Uri's that will be uploaded
    private static int DEFAULT_IMAGE_SELECT_RESOURCE = R.mipmap.ic_launcher_round;

    private static final int IMAGE_GALLERY_RESULT = 3141;

    //Pet detail variables
    private String dateLost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        initUI();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_pet_upload_one:

                //TODO reevaluate case statements
                break;
            case R.id.add_pet_upload_two:
                Log.e("yeet", "yyet");
                startGalleryIntent();
                break;
            case R.id.add_pet_upload_three:
                startGalleryIntent();
                break;
            case R.id.add_pet_image_select:
                startGalleryIntent();
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

        }

    }
    /*
    Initializes views and sets on click listeners
     */
    private void initUI(){
        mSpinnerList = new ArrayList<>();
        mSpinnerList.add("Today");
        mSpinnerList.add("Set Date");
        mDateSpinner = findViewById(R.id.add_pet_date_spinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSpinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDateSpinner.setAdapter(adapter);
        mDateSpinner.setOnItemSelectedListener(this);
        mImageToUpload3 = findViewById(R.id.add_pet_upload_three);
        mImageToUpload3.setOnClickListener(this);
        mImageToUpload = findViewById(R.id.add_pet_upload_one);
        mImageToUpload.setOnClickListener(this);
        mImageToUpload2 = findViewById(R.id.add_pet_upload_two);
        mImageToUpload2.setOnClickListener(this);
        mImageToUpload3.setOnClickListener(this);
        mPetNameEditText = findViewById(R.id.add_pet_name);
        mPetZipEditText = findViewById(R.id.add_pet_zip);
        mPetDescriptionEditText = findViewById(R.id.add_pet_description);
        mSelectImageButton = findViewById(R.id.add_pet_image_select);
        mSelectImageButton.setOnClickListener(this);
        mImageCancel = findViewById(R.id.add_pet_cancel_image_one);
        mImageCancel.setOnClickListener(this);
        mImageCancelTwo = findViewById(R.id.add_pet_cancel_image_two);
        mImageCancelTwo.setOnClickListener(this);
        mImageCancelThree = findViewById(R.id.add_pet_cancel_image_three);
        mImageCancelThree.setOnClickListener(this);
        imageUriList = new LinkedList<>();

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
            chooseDateDialogFragment.show(getFragmentManager(), "chooseDate");
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


    //TODO test all of the coded after this line

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_GALLERY_RESULT && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            addImageToList(uri);
        }
    }

    private void addImageToList(Uri uri){
        if(imageUriList.size() == 0) {
            imageUriList.add(uri);
            mImageToUpload.setVisibility(View.VISIBLE);
            mSelectImageButton.setVisibility(View.GONE);
            mImageCancel.setVisibility(View.VISIBLE);
            mImageToUpload2.setVisibility(View.VISIBLE);
            Glide.with(this).load(uri).into(mImageToUpload);
        }
        else if(imageUriList.size() == 1){
            imageUriList.add(uri);
            mImageCancelTwo.setVisibility(View.VISIBLE);
            mImageToUpload3.setVisibility(View.VISIBLE);
            mImageCancelThree.setVisibility(View.VISIBLE);
            Glide.with(this).load(uri).into(mImageToUpload2);
        }
        else{
            mImageCancelThree.setVisibility(View.VISIBLE);
            imageUriList.add(uri);
            Glide.with(this).load(uri).into(mImageToUpload3);
        }

    }

    private void removeImageFromList(int position){
        switch(position){
            case 0:
                imageUriList.remove(0);
                mImageToUpload.setImageResource(DEFAULT_IMAGE_SELECT_RESOURCE);
                break;
            case 1:
                imageUriList.remove(1);
                mImageToUpload2.setImageResource(DEFAULT_IMAGE_SELECT_RESOURCE);
                break;
            case 2:
                imageUriList.remove(2);
                mImageToUpload.setImageResource(DEFAULT_IMAGE_SELECT_RESOURCE);
                break;
        }
    }

    private void startGalleryIntent(){
        Intent searchGalleryIntent = new Intent();
        searchGalleryIntent.setType("image/*");
        searchGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(searchGalleryIntent,"Select Picture"), IMAGE_GALLERY_RESULT);
    }
}
