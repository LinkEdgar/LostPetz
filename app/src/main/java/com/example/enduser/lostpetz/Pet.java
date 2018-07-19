package com.example.enduser.lostpetz;

import android.os.Parcel;
import android.os.Parcelable;

public class Pet implements Parcelable{

    public Pet(){

    }
    public Pet(String name, String description, boolean foundPet){
        this.name = name;
        this.description = description;
        this.foundPet = foundPet;
    }

    public Pet(String name, String description, boolean foundPet, String profileUrlOne, String profileUrlTwo, String profileUrlThree,String breed, String zip){
        this.name = name;
        this.description = description;
        this.foundPet = foundPet;
        this.profileUrlOne = profileUrlOne;
        this.profileUrlTwo = profileUrlTwo;
        this.profileUrlThree = profileUrlThree;
        this.breed = breed;
        this.zip = zip;
    }

    private String profileUrlOne;
    private String profileUrlTwo;
    private String profileUrlThree;
    private String name;
    private String zip;
    private String dateLost;
    private String description;
    private boolean foundPet;
    private String breed;
    private String userID;


    // setter methods

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setProfileUrlThree(String profileUrlThree) {
        this.profileUrlThree = profileUrlThree;
    }

    public void setProfileUrlOne(String profileUrlOne) {
        this.profileUrlOne = profileUrlOne;
    }

    public void setProfileUrlTwo(String profileUrlTwo) {
        this.profileUrlTwo = profileUrlTwo;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setFoundPet(boolean foundPet) {
        this.foundPet = foundPet;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrlOne = profileUrl;
    }

    public void setName(String name){this.name = name;}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setZip(String zip){this.zip = zip;}
    public void setDateLost(String dateLost){this.dateLost = dateLost;}
    //getter methods
    public String getName(){return name;}

    public String getZip() {
        return zip;
    }

    public String getDateLost() {
        return dateLost;
    }

    public String getDescription() {
        return description;
    }
    public boolean getIsFoundPet() {
        return foundPet;
    }

    public String getProfileUrl() {
        return profileUrlOne;
    }

    public String getBreed() {
        return breed;
    }

    public String getProfileUrlTwo() {
        return profileUrlTwo;
    }

    public String getProfileUrlThree() {
        return profileUrlThree;
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(profileUrlOne);
        parcel.writeString(profileUrlTwo);
        parcel.writeString(profileUrlThree);
        parcel.writeString(name);
        parcel.writeString(zip);
        parcel.writeString(dateLost);
        parcel.writeString(description);
        parcel.writeByte((byte) (foundPet ? 1 : 0));
        parcel.writeString(breed);
        parcel.writeString(userID);
    }

    protected Pet(Parcel in) {
        profileUrlOne = in.readString();
        profileUrlTwo = in.readString();
        profileUrlThree = in.readString();
        name = in.readString();
        zip = in.readString();
        dateLost = in.readString();
        description = in.readString();
        foundPet = in.readByte() != 0;
        breed = in.readString();
        userID = in.readString();
    }

    public static final Creator<Pet> CREATOR = new Creator<Pet>() {
        @Override
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        @Override
        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };
}
