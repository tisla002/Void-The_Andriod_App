package com.example.starw.chatapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserandVideoModel{
    public String user;
    public String Vid;
    public String type;

    public UserandVideoModel(){
        //default, Snape Kills Dumbledore
    }

    public UserandVideoModel(String username, String url){

        this.user = username;
        this.Vid = url;
        this.type = "Picture";

    }

}