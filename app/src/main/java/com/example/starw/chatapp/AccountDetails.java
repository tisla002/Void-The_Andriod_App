package com.example.starw.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;

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

                if (username_text.compareTo("") == 0) {
                    username.setError("Username can't be blank");
                } else if (username_text.length() < 4) {
                    username.setError("Username must be at least 4 characters.");
                } else if (!username_text.matches("[A-Za-z0-9]+")) {
                    username.setError("Username must be alphanumeric.");
                } else {
                    UserDataModel dataModel = new UserDataModel(username_text);
                    database.getReference("users")
                            .child(uid)
                            .setValue(dataModel);

                    database.getReference().addChildEventListener(
                            new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            boolean fail = false;
                            int count = 0;

                            for (DataSnapshot data: dataSnapshot.getChildren()) {
                                // If the current username has already been alert the user and
                                // return.
                                if (data.getRef().getParent().getKey().compareTo("users") == 0) {
                                    if (data.child("username")
                                            .getValue(String.class)
                                            .compareTo(username_text) == 0) {
                                        if (count >= 1) {
                                            username.setError("Username taken.");
                                            data.getRef().removeValue();

                                            fail = true;
                                            break;
                                        } else {
                                            count++;
                                        }
                                    }
                                }
                            }

                            if (!fail) {
                                Toast.makeText(AccountDetails.this,
                                        "Username successfully added!",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(AccountDetails.this,
                                        Users.class)
                                );
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
