package com.example.starw.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText username, password;
    Button registerButton;
    String user, pass;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        login = findViewById(R.id.login);

        Firebase.setAndroidContext(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance(
                        "https://void-app-5369d.firebaseio.com/");
                final DatabaseReference user_db = database.getReference().child("users");

                user = username.getText().toString();
                pass = password.getText().toString();

                if (user.equals("")){
                    username.setError("can't be blank");
                } else if(pass.equals("")){
                    password.setError("can't be blank");
                } else if(!user.matches("[A-Za-z0-9]+")){
                    username.setError("only alphabet or number allowed");
                } else if(user.length() < 5){
                    username.setError("at least 5 characters long");
                } else if(pass.length() < 5){
                    password.setError("at least 5 characters long");
                } else {
                    final ProgressDialog pd = new ProgressDialog(Register.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    Models add_user = new Models(user, pass);
                    user_db.push().setValue(add_user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError err, DatabaseReference ref) {
                            pd.dismiss();
                        }
                    });

                    user_db.addListenerForSingleValueEvent(
                            new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean registrationFail = false;

                            for (DataSnapshot data: dataSnapshot.getChildren()) {
                                for (DataSnapshot user_items: data.getChildren()) {
                                    if (user_items.getValue() == user) {
                                        Toast.makeText(Register.this,
                                                "Username Taken.",
                                                Toast.LENGTH_LONG).show();

                                        registrationFail = true;
                                        break;
                                    }
                                }

                                if (registrationFail) {
                                    data.getRef().removeValue();
                                }
                            }

                            if (!registrationFail) {
                                Toast.makeText(Register.this,
                                                "Registration Successful!",
                                                Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}