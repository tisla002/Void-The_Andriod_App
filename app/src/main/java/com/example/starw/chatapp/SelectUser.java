package com.example.starw.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectUser extends AppCompatActivity {

    ImageView profilepics;
    ListView userNames;
    Button button;
    ArrayList<String> userList;
    ArrayAdapter<String> adapter;
    //private CheckAdapter adapter;
    userObject users = new userObject();
    String stuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        final Intent intent = getIntent();
        final String username_c = intent.getStringExtra("username");

        profilepics = (ImageView) findViewById(R.id.profileImage);
        userNames = (ListView) findViewById(R.id.member_names);
        button = findViewById(R.id.button);
        userList = new ArrayList<>();


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://void-app-5369d.firebaseio.com/");
        final DatabaseReference user_db = database.getReference().child("users");

        final DatabaseReference dataRef = database.getReference().child("threads");
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        final DatabaseReference currentUser = user_db.child(uid);

        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stuff = dataSnapshot.child("username").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(SelectUser.this, stuff, Toast.LENGTH_SHORT).show();



        user_db.addListenerForSingleValueEvent(
                new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String name = data.child("username").getValue(String.class);

                            userList.add(name);
                        }
                        adapter = new ArrayAdapter<>(SelectUser.this, android.R.layout.simple_list_item_checked, userList);
                        userNames.setAdapter(adapter);
                        userNames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        userNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                CheckedTextView check = (CheckedTextView)view;
                                if (check.isChecked()){
                                    userNames.setItemChecked(position, true);
                                }else{
                                    userNames.setItemChecked(position, false);
                                }


                            }
                        });



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference data = dataRef.push();
                final String thread = data.getKey();

                final SparseBooleanArray sp = userNames.getCheckedItemPositions();
                for (int i = 0; i < sp.size(); i++) {
                    users.add(userList.get(sp.keyAt(i)).toString());

                }

                users.add(stuff);

                final ArrayList<String> arr2 = userList;

                user_db.addListenerForSingleValueEvent(
                        new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    String name = data.child("username").getValue(String.class);
                                    DatabaseReference dRef = data.getRef();

                                    for (int i = 0; i < sp.size(); i++) {
                                        String temp = userList.get(sp.keyAt(i));

                                        if(name.compareTo(temp) == 0) {
                                            dRef.child("threads").push().setValue(thread);
                                        }
                                    }
                                }
                             currentUser.child("threads").push().setValue(thread);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                data.child("users").setValue(users);
                Intent main = new Intent(SelectUser.this, Chat.class);
                main.putExtra("username", username_c);
                main.putExtra("thread_id", thread);

                startActivity(main);
            }
        });

        profilepics.setImageResource(R.drawable.no_user);

    }

}