package com.example.enduser.lostpetz;

public class Pet {
    public Pet(){

    }
    public Pet(String name, String description, boolean foundPet){
        this.name = name;
        this.description = description;
        this.foundPet = foundPet;
    }

    private String profileUrl;
    private String name;
    private String zip;
    private String dateLost;
    private String description;
    private boolean foundPet;

    // setter methods


    public void setFoundPet(boolean foundPet) {
        this.foundPet = foundPet;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
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
        return profileUrl;
    }
}
