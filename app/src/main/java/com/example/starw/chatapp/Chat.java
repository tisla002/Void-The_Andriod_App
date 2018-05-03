package com.example.starw.chatapp;

//comment test

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;



public class Chat extends AppCompatActivity {
    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    //Firebase reference1, reference2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout) findViewById(R.id.layout1);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://void-app-5369d.firebaseio.com/");
        final DatabaseReference dataRef = database.getReference().child("threads");

        //reference1 = new Firebase("https://void-app-5369d.firebaseio.com/" + UserDetails.username + "_" + UserDetails.chatWith);
        //reference2 = new Firebase("https://void-app-5369d.firebaseio.com/" + UserDetails.chatWith + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    HashMap<String, String> mess = new HashMap<String, String>();
                    HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
                    HashMap<String, String> users = new HashMap<String, String>();

                    users.put("user", UserDetails.chatWith);
                    mess.put("user", UserDetails.username);
                    mess.put("text", messageText);
                    map.put("user", users);
                    map.put("message", mess);


                    dataRef.push().setValue(map);

                    messageArea.setText("");
                }
            }


        });


        dataRef.addListenerForSingleValueEvent(
                new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String x = data.child("messages").child("text").getValue(String.class);

                            String sender = data.child("user").child("user").getValue(String.class);
                            String recieve = data.child("messages").child("user").getValue(String.class);

                            if (sender.equals(UserDetails.username)) {
                               addMessageBox("You:-\n" + x, 1);
                            } else {
                                addMessageBox(recieve + ":-\n" + x, 2);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(Chat.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);

        if (type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        } else {
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }


}