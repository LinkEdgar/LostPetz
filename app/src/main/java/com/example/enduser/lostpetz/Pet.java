package com.example.enduser.lostpetz;

public class Pet {
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


    // setter methods


    public void setProfileUrlThree(String profileUrlThree) {
        this.profileUrlThree = profileUrlThree;
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
}
