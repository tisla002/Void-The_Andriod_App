package com.example.starw.chatapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDataModel {
    // Model that instantiates new object for userdata within Firebase database.
    public String username;

    public UserDataModel() {

    }

    public UserDataModel(String username) {
        this.username = username;
    }
}
