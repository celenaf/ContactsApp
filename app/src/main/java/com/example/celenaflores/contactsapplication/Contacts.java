package com.example.celenaflores.contactsapplication;

/**
 * Created by celee on 1/24/18.
 */

public class Contacts {
    //Return the resource ID representing the contact's name
    public int getmNameResId() {
        return mNameResId;
    }

    //Set the resource ID representing the contact's name
    public void setmNameResId(int mNameResId) {
        this.mNameResId = mNameResId;
    }

    //Refers to a string defined in strings.xml
    private int mNameResId; //not storing actual text here!

    //Return the resource ID representing the contact's photo
    public int getmImageResId() {
        return mImageResId;
    }

    //Set the resource ID representing the contact's photo
    public void setmImageResId(int mImageResId) {
        this.mImageResId = mImageResId;
    }

    //Refers to an image defined in res/drawable (one image for all DPIs for now)
    private int mImageResId; //not storing actual images here!

    //A user-defined location, we'll store the string the user enters in this object
    // and back it up to a file for persistent storage
    private String mLocation;

    //Return the contact's current location (set by user)
    public String getLocation() {
        return mLocation;
    }

    //Set the contact's current location (user-defined)
    public void setLocation(String location) {
        this.mLocation = location;
    }

    //Return the resource ID representing the contact's age
    public int getmAgeResId() { return mAgeResId; }

    //Set the resource ID representing the contact's age
    public void setmAgeResId(int mAgeResId) {
        this.mAgeResId = mAgeResId;
    }

    //Refers to a string defined in strings.xml
    private int mAgeResId; //not storing actual text here!

    //Constructor for a contact sets its name and image resources by parameter,
    // but always sets the location to ??? initially
    public Contacts(int nameResId, int imageResId, int ageResId) {
        mNameResId = nameResId;
        mImageResId = imageResId;
        mAgeResId = ageResId;
        mLocation = "???"; //initially user doesn't know location
    }


}
