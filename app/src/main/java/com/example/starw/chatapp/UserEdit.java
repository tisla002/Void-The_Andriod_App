package com.example.starw.chatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserEdit extends AppCompatActivity {
    private TextView myName;
    private ImageView myProfileImage;
    private TextView myUserName;
    private Button myReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        myName = (TextView) findViewById(R.id.Name);
        myProfileImage = (ImageView) findViewById(R.id.Image);
        myUserName = (TextView) findViewById(R.id.Username);
        myReturnButton = (Button) findViewById(R.id.Return);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://void-app-5369d.firebaseio.com/");
        DatabaseReference user_db = database.getReference().child("users");

        user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String name = data.child("firstname").getValue(String.class);
                    String username = data.child("username").getValue(String.class);
//                    String picture = data.child("picture").getValue(String.class);

                    myName.setText(name);
                    myUserName.setText(username);
                    myProfileImage.setImageResource(R.drawable.no_user);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}