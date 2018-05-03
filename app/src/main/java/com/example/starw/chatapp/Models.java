package com.example.starw.chatapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Models {
    public String firstname;
    public String lastname;
    public String username;
    public String password;

    public Models() {
        // Default constructor.
    }

    public Models(String username, String password) {
        this.username = username;
        this.firstname = username;
        this.lastname = username;
        this.password = password;
    }
}