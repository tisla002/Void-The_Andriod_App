
package com.example.starw.chatapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDataModel {
    // Model that instantiates new object for userdata within Firebase database.
    public String username;
    public String image_loc;
    public String profileImg;
    public String online;

    public UserDataModel() {

    }

    public UserDataModel(String username, String image_loc, String profileImg, String online) {

        this.username = username;
        this.image_loc = image_loc;
        this.profileImg = profileImg;
        this.online = online;
    }
}