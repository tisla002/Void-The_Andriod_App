package com.example.starw.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

public class User_Edit extends AppCompatActivity {
    EditText passwordChange;
    Button saveButton;
    String user, pass;
    TextView chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //usernameChange = (EditText)findViewById(R.id.usernameChange);
        passwordChange = (EditText)findViewById(R.id.passwordChange);
        saveButton = (Button)findViewById(R.id.saveButton);
        chat = (TextView)findViewById(R.id.chat);

        Firebase.setAndroidContext(this);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User_Edit.this, Users.class));
            }
        });
    }
}