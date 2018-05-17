package com.example.starw.chatapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserandPicModel{
    public String user;
    public String Pic;
    public String type;

    public UserandPicModel(){
        //default, Snape Kills Dumbledore
    }

    public UserandPicModel(String username, String url){

        this.user = username;
        this.Pic = url;
        this.type = "Picture";

    }

}