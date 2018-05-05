package com.example.enduser.lostpetz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class PetQueryActivity extends AppCompatActivity implements PetAdapter.onViewClicked{
    //Recycler view
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PetAdapter mPetAdapter;
    private ArrayList<Pet> mPetArrrayList;

    //
    private ProgressBar mNoMessageProgressBar;
    private SearchView mSearchView;
    //this variable check if the user is submitting the same query twice so that
    //we don't do two network calls
    private boolean isDoubleSubmit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_query);
        //Variable instances
        //TODO appropriately display this when there are no messages
        mNoMessageProgressBar = findViewById(R.id.pet_query_progressbar);
        mSearchView = findViewById(R.id.pet_query_searchview);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!isDoubleSubmit && s.length() > 0){
                    submitSearchQuery();
                    isDoubleSubmit = true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                isDoubleSubmit = false;
                return false;
            }
        });
        mRecyclerView = findViewById(R.id.pet_query_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPetArrrayList = new ArrayList<>();
        //Fake info
        for(int x =0; x< 10; x++){
            boolean isFound;
            if(x %2 ==0){
                isFound = false;
            }
            else {
                isFound = true;
            }
        mPetArrrayList.add(new Pet(" "+ x, "Pet description goes here", isFound));
        }
        mPetAdapter = new PetAdapter(mPetArrrayList);
        mPetAdapter.setOnViewClicked(this);
        mRecyclerView.setAdapter(mPetAdapter);

        }
        private void submitSearchQuery(){
            Toast.makeText(this, "Search Query Submitted", Toast.LENGTH_SHORT).show();
            //TODO in the query make all characters lowercase so that the searches are optimized
        }

    @Override
    public void onClick(int position) {
        //TODO fill with actual logic and remove toast
        Toast.makeText(this, "clicked on item " + position , Toast.LENGTH_SHORT).show();
    }
}
