package com.example.enduser.lostpetz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
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


/*
    This activity does allows the user to search through lost pets with filters. The two key filters being name and breed have been set to lower
    case for search optimization but are formatted when they are put in the PetAdapter. These values cannot be null as they will cause a null pointer
    exception.
 */

public class PetQueryActivity extends AppCompatActivity implements PetAdapter.onViewClicked{

    public static final String SEARCH_FILTER_NAME = "name";
    public static final String SEARCH_FILTER_ZIP = "zip";
    public static final String SEARCH_FILTER_BREED = "breed";
    private static final int NO_PETS_FOUND = 0;
    //Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener mListener;

    //Recycler view
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PetAdapter mPetAdapter;
    private ArrayList<Pet> mPetArrrayList;

    //Views
    private TextView mNoPetsFoundTextView;
    private ProgressBar mNoPetsProgressBar;
    private SearchView mSearchView;
    private Button mNameFilterButton;
    private Button mZipFilterButton;
    private Button mBreedFilterButton;
    //this variable check if the user is submitting the same query twice so that
    //we don't do two network calls
    private boolean isDoubleSubmit = false;
    private CardView mCardView;
    private String searchFilterType = SEARCH_FILTER_NAME;

    private HashSet<String> mPetKeyHashset;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pet_query);
            //firebase instances
            mDatabase = FirebaseDatabase.getInstance();
            mRef = mDatabase.getReference(FirebaseValues.FirebaseDatabaseValues.FIREBASE_PETS_ROOT);
            initUIViewsAndVariables();
            }



        /*
        Takes a query from the user to query it from the realtime database. The old query is
        erased
         */
        private void submitSearchQuery(String string){
            mPetArrrayList.clear();
            mPetKeyHashset.clear();
            mPetAdapter.notifyDataSetChanged();
            String query = string.toLowerCase().trim();
            mNoPetsProgressBar.setVisibility(View.VISIBLE);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    checkIfPetsFound();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mRef.orderByChild(searchFilterType).startAt(query).endAt(query).addChildEventListener(
                    mListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    addPetsFromSnapshot(dataSnapshot);
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
            });

        }

    /*
    This is the onClick interface from the PetAdapter to allow onClick handling
     */
    @Override
    public void onClick(int position) {
        //TODO fill with actual logic and remove toast

        Toast.makeText(this, "clicked on item " + position, Toast.LENGTH_SHORT).show();

    }
    /*
    This method takes in a snapshot and checks its keys to a hashset as to not allow for duplicate values
    it also formats the name and breed of the pet as this data is defaulted to lowercase for search efficiency.
     */
    private void addPetsFromSnapshot(DataSnapshot snapshot){
            String key = snapshot.getKey();
            if(!mPetKeyHashset.contains(key)) {
                mPetKeyHashset.add(key);
                Pet pet = snapshot.getValue(Pet.class);

                String lowerCaseName = pet.getName();
                String formattedName = lowerCaseName.substring(0,1).toUpperCase()
                        + lowerCaseName.substring(1,lowerCaseName.length());
                pet.setName(formattedName);

                String lowerCaseBreed = pet.getBreed();
                String formattedBreed = lowerCaseBreed.substring(0,1).toUpperCase()
                        + lowerCaseBreed.substring(1,lowerCaseBreed.length());
                pet.setBreed(formattedBreed);

                mPetArrrayList.add(pet);
                mPetAdapter.notifyItemChanged(mPetArrrayList.size());
            }

    }
    /*
    This method sets the filter type when the user
     */
    public void chooseFilterType(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.pet_query_name_filter:
                searchFilterType = SEARCH_FILTER_NAME;
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mBreedFilterButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mZipFilterButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
            case R.id .pet_query_breed_filter:
                searchFilterType = SEARCH_FILTER_BREED;
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mZipFilterButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mNameFilterButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
            case R.id.pet_query_zip_filter:
                searchFilterType = SEARCH_FILTER_ZIP;
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mBreedFilterButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mNameFilterButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;
        }
    }

    /*
    Initializes all UI elements and variable instances
     */
    private void initUIViewsAndVariables(){
        //Variable instances
        mNoPetsFoundTextView = findViewById(R.id.pet_query_no_pets_textview);
        mPetKeyHashset = new HashSet<>();
        mCardView = findViewById(R.id.pet_query_filter_cardview);
        mNoPetsProgressBar = findViewById(R.id.pet_query_progressbar);
        mNameFilterButton = findViewById(R.id.pet_query_name_filter);
        mZipFilterButton = findViewById(R.id.pet_query_zip_filter);
        mBreedFilterButton = findViewById(R.id.pet_query_breed_filter);
        mSearchView = findViewById(R.id.pet_query_searchview);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!isDoubleSubmit && s.length() > 0){
                    submitSearchQuery(s);
                    isDoubleSubmit = true;
                    mCardView.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mCardView.setVisibility(View.VISIBLE);
                isDoubleSubmit = false;
                return false;
            }
        });
        mRecyclerView = findViewById(R.id.pet_query_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPetArrrayList = new ArrayList<>();

        mPetAdapter = new PetAdapter(mPetArrrayList);
        mPetAdapter.setOnViewClicked(this);
        mRecyclerView.setAdapter(mPetAdapter);
    }

    /*
    Adds the child listener when the activity is restarted
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(mListener != null) {
            mRef.addChildEventListener(mListener);
        }
    }

    /*
    Removes the child event listener when the user is no longer using the application
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(mListener != null){
            mRef.addChildEventListener(mListener);
        }
    }
    /*
    called on after from the single event listener to ensure the UI reflects the
    query status.
     */
    private void checkIfPetsFound(){
        mNoPetsProgressBar.setVisibility(View.GONE);
        if(mPetArrrayList.size() == NO_PETS_FOUND){
            mNoPetsFoundTextView.setVisibility(View.VISIBLE);
        }
        else{
            mNoPetsFoundTextView.setVisibility(View.GONE);
        }
    }

}
