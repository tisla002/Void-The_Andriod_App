package com.example.starw.chatapp;

import java.util.ArrayList;

public class userObject {
    public ArrayList<String> user;

    public userObject(){
        //default, Snape Kills Dumbledore
        user = new ArrayList<String>();
    }

    public void add(String temp) {
        user.add(temp);
    }
}