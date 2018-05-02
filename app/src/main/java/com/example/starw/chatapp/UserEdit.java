package com.example.starw.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserEdit extends AppCompatActivity {
    private TextView myName;
    private ImageView myProfileImage;
    private TextView myUserName;
    private TextView myPassword;
    private Button mySaveChanges;
    private EditText myPasswordChange;
    private Button mySaveButton;
    private Button myReturnButton;

    private DatabaseReference myUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String user_id = getIntent().getStringExtra("user_id");

        myUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        myName = (TextView) findViewById(R.id.textView);
        myProfileImage = (ImageView) findViewById(R.id.imageView2);
        myUserName = (TextView) findViewById(R.id.textView2);
        myPassword = (TextView) findViewById(R.id.textView3);
        myPasswordChange = (EditText) findViewById(R.id.passwordChange);
        mySaveButton = (Button) findViewById(R.id.saveButton);
        myReturnButton = (Button) findViewById(R.id.button2);

        //Firebase.setAndroidContext(this);

        myUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue().toString();
//                String displayImage = dataSnapshot.child("image").getValue().toString();
                String displayUserName = dataSnapshot.child("username").getValue().toString();


                myName.setText(displayName);
                myUserName.setText(displayUserName);
                myProfileImage.setImageResource(R.drawable.stevejobs);

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