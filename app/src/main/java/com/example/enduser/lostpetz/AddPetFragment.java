package com.example.enduser.lostpetz;

import android.app.Activity;
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

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddPetFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, ChooseDateDialogFragment.onClicked{
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
                Log.e("yeet", "yyet");
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
            addImageToList(uri,imageSelectorPosition);
        }
    }

    /*
    Sets views based on the size of imageUriList. Currently there is only support for a maximum of
    three images per pet entry into the DB.
     */
    private void addImageToList(Uri uri, int position){
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
}
