package com.example.starw.chatapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserandTextModel {
    public String user;
    public String text;
    public String type;

    public UserandTextModel(){
        //default, Snape Kills Dumbledore
    }

    public UserandTextModel(String username, String message){

        this.user = username;
        this.text = message;
        this.type = "text";


    }


}