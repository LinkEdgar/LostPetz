package com.example.enduser.lostpetz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class PetQueryActivity extends AppCompatActivity {
    //Recycler view
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PetAdapter mPetAdapter;
    private ArrayList<Pet> mPetArrrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_query);
        //Variable instances
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
        mRecyclerView.setAdapter(mPetAdapter);


    }
}
