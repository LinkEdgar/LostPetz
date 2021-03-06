package com.example.enduser.lostpetz.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.enduser.lostpetz.CustomObjectClasses.Pet;
import com.example.enduser.lostpetz.R;

import java.util.ArrayList;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.ViewHolder>{
    private ArrayList<Pet> mArrayList;
    private onViewClicked mClicked;
    //Displays whether the pet was reported as lost by the poster or found
    private static String PET_STATUS_LOST = "Lost on: ";
    private static String PET_STATUS_FOUND= "Found on: ";

    public PetAdapter(ArrayList<Pet> arrayList){
        mArrayList = arrayList;
    }
    public interface onViewClicked{
        void onClick(int position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View petView = inflater.inflate(R.layout.pet_container,parent,false);
        PetAdapter.ViewHolder viewHolder = new PetAdapter.ViewHolder(petView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        Pet pet = mArrayList.get(position);
        holder.name.setText(pet.getName());
        holder.description.setText(pet.getDescription());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClicked.onClick(position);
            }
        });
        if(pet.getProfileUrlOne() != null) {
            Glide.with(holder.profilePicture).applyDefaultRequestOptions(new RequestOptions().error(R.mipmap.ic_launcher_round)
                    .centerCrop()).load(pet.getProfileUrlOne()).into(holder.profilePicture);
        }

        if(pet.getIsFoundPet()) {
            holder.date.setText(PET_STATUS_FOUND+ pet.getDateLost());
        }
        else{
            holder.date.setText(PET_STATUS_LOST +pet.getDateLost());

        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profilePicture;
        public TextView description;
        public TextView name;
        public TextView date;
        public View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            profilePicture = itemView.findViewById(R.id.pet_container_profile_picture);
            description = itemView.findViewById(R.id.pet_container_description);
            name = itemView.findViewById(R.id.pet_container_pet_name);
            date = itemView.findViewById(R.id.pet_container_date_lost);
        }
    }

    /*
    sets the onViewClicked interface to enable onclick listeners for the view in the
    recyclerview
     */
    public void setOnViewClicked(onViewClicked click){
        mClicked = click;
    }
}
