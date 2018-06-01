package com.example.starw.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class AccountDetails extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        // Get button and textfield values.
        final TextView username = findViewById(R.id.usernameField);
        Button submit = findViewById(R.id.submitButton);

        // Firebase database and auth parameters.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username_text = username.getText().toString();
                // Where images will be stored
                final String image_loc = "gs://void-app-5369d.appspot.com/profile_Img/" + username_text;
                // Current profile Image. Loads no_user.png by default
                final String profileImg = "gs://void-app-5369d.appspot.com/profile_Img/no_user.png";
                final String online = "false";

                if (username_text.compareTo("") == 0) {
                    username.setError("Username can't be blank");
                } else if (username_text.length() < 4) {
                    username.setError("Username must be at least 4 characters.");
                } else if (!username_text.matches("[A-Za-z0-9]+")) {
                    username.setError("Username must be alphanumeric.");
                } else {
                    database.getReference("users").addChildEventListener(
                            new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (TextUtils.isEmpty(s)) {
                                boolean fail = false;

                                for (DataSnapshot data: dataSnapshot.getChildren()) {
                                    if (data.getKey().compareTo("username") == 0) {
                                        if (data.getValue(String.class)
                                                .compareTo(username_text) == 0) {
                                            // Set warning.
                                            username.setError("Username taken!");

                                            // Prevent data from being added.
                                            fail = true;
                                            break;
                                        }
                                    }
                                }

                                if (!fail) {
                                    // Only run if username hasn't been taken.
                                    UserDataModel dataModel = new UserDataModel(username_text, image_loc, profileImg, online);
                                    database.getReference("users")
                                            .child(uid)
                                            .setValue(dataModel);

                                    Toast.makeText(AccountDetails.this,
                                            "Registration successful!",
                                            Toast.LENGTH_SHORT).show();

                                    // Go to threads display.
                                    startActivity(
                                            new Intent(
                                                    AccountDetails.this,
                                                    Users.class
                                            )
                                    );
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        });
    }
}
