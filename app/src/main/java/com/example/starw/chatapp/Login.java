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

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//
//import org.json.JSONException;
//import org.json.JSONObject;

public class Login extends AppCompatActivity {
    TextView register;
    EditText username, password;
    Button loginButton;
    String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://void-app-5369d.firebaseio.com/");
                final DatabaseReference user_db = database.getReference().child("users");

                user = username.getText().toString();
                pass = password.getText().toString();

                if (user.equals("")) {
                    username.setError("Can't be blank");
                } else if (pass.equals("")) {
                    password.setError("Can't be blank");
                } else {
                    String url = "https://void-app-5369d.firebaseio.com/.json";
                    final ProgressDialog pd = new ProgressDialog(Login.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    user_db.addListenerForSingleValueEvent(
                            new com.google.firebase.database.ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean login_user = false;
                                    boolean login_pass = false;

                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        String name = data.child("username").getValue(String.class);
                                        String password = data.child("password").getValue(String.class);

                                        if (name.compareTo(user) == 0) {
                                            login_user = true;


                                            if (password.compareTo(pass) == 0) {
                                                login_pass = true;

                                                Toast.makeText(Login.this,
                                                        "Signed In",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(Login.this,
                                                        "Username or Password Does Not Exist.",
                                                        Toast.LENGTH_LONG).show();
                                            }

                                        }

                                    }

                                    if(login_pass == true && login_user == true){
                                        Intent main = new Intent(Login.this, Users.class);
                                        main.putExtra("username", user);
                                        startActivity(main);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    pd.dismiss();


                }
            }
        });
    }

}