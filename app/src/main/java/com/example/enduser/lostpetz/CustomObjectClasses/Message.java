package com.example.enduser.lostpetz.CustomObjectClasses;

/**
 * Created by EndUser on 4/29/2018.
 */

public class Message {
    private String userName;
    private String message;
    private String photoUrl; //pictures sent
    private String profileUrl;

    //firebase requieres empty constructor
    public Message(){

    }

    public Message(String userName, String message, String photoUrl, String userProfileUrl){
        this.userName = userName;
        this.message = message;
        this.photoUrl = photoUrl;
        this.profileUrl = userProfileUrl;
    }
    public void setUserName(String userName){this.userName = userName;}
    public void setMessage(String message){this.message = message;}
    public void setPhotoUrl(String photoUrl){this.photoUrl = photoUrl;}
    public void setUserProfileUrl(String userProfileUrl){this.profileUrl = userProfileUrl;}
    public String getUserName(){return userName;}
    public String getMessage(){return message;}
    public String getPhotoUrl(){return photoUrl;}
    public String getUserProfileUrl(){return profileUrl;}

}
