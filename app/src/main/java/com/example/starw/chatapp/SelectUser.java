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

import java.util.ArrayList;
import java.util.List;

public class SelectUser extends AppCompatActivity {

    ImageView profilepics;
    ListView userNames;
    Button button;
    ArrayList<String> userList;
    ArrayAdapter<String> adapter;
    //private CheckAdapter adapter;

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

        //final FirebaseDatabase database = FirebaseDatabase.getInstance("https://void-app-5369d.firebaseio.com/");
        final DatabaseReference dataRef = database.getReference().child("threads");
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference currentUser = user_db.child(uid);



        user_db.addListenerForSingleValueEvent(
                new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String name = data.child("username").getValue(String.class);
                            //if(name == currentUser) {
                            //    userList.add(name);
                            //}

                            userList.add(name);
                        }
                        adapter = new ArrayAdapter<>(SelectUser.this, android.R.layout.simple_list_item_checked, userList);
                        //adapter = new CheckAdapter(SelectUser.this, userList);
                        userNames.setAdapter(adapter);
                        userNames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        userNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(SelectUser.this, userList.get(position), Toast.LENGTH_SHORT).show();
                                //SparseBooleanArray sparseBooleanArray = userNames.getCheckedItemPositions();
                                //Toast.makeText(SelectUser.this, "Clicked Position := "+position +" Value: ", Toast.LENGTH_SHORT).show();
                                CheckedTextView check = (CheckedTextView)view;
//                                check.setChecked(!check.isChecked());
                                if (check.isChecked()){
                                    userNames.setItemChecked(position, true);
                                }else{
                                    userNames.setItemChecked(position, false);
                                }
                                //userNames.setItemChecked(position, !userNames.isItemChecked(position));
//                                userNames.setItemChecked(position, true);
//                                ArrayList<String> listy = new ArrayList<>();
//                                SparseBooleanArray sp = userNames.getCheckedItemPositions();
//                                for (int i = 0; i < sp.size(); i++) {
//                                    listy.add(userList.get(sp.keyAt(i)));
//                                }
//                                final FirebaseDatabase database = FirebaseDatabase.getInstance("https://void-app-5369d.firebaseio.com/");
//                                final DatabaseReference dataRef = database.getReference().child("threads");
                                //Toast.makeText(SelectUser.this, Boolean.toString(userNames.isItemChecked(position)) , Toast.LENGTH_SHORT).show();
                                //Toast.makeText(SelectUser.this, Integer.toString(userNames.getCheckedItemCount()) , Toast.LENGTH_SHORT).show();
                                //Toast.makeText(SelectUser.this, userNames.getCheckedItemPositions().toString() , Toast.LENGTH_SHORT).show();
                                //Toast.makeText(SelectUser.this, listy.toString() , Toast.LENGTH_SHORT).show();
//                                threadModel newUser = new threadModel();
//                                dataRef.push().child("messages").push().setValue(newUser);

                                //userList.get(sp.keyAt(i));

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
                //threadModel newUser = new threadModel();
                DatabaseReference data = dataRef.push();
                final String thread = data.getKey();
                //data.child("messages").push().setValue(newUser);
                userObject users = new userObject();
                //threadModel newUser = new threadModel();
                //dataRef.push().child("messages").push().setValue(newUser);

                //Toast.makeText(SelectUser.this, Integer.toString(userNames.getCheckedItemCount()) , Toast.LENGTH_SHORT).show();
                //Toast.makeText(SelectUser.this, userNames.getCheckedItemPositions().toString() , Toast.LENGTH_SHORT).show();

                final SparseBooleanArray sp = userNames.getCheckedItemPositions();
                for (int i = 0; i < sp.size(); i++) {
                    //Log.d("DO:", "String sent: " + userList.get(sp.keyAt(i)));
                    users.add(userList.get(sp.keyAt(i)).toString());

                }
                //users.add(username_c);
                //String cur = currentUser.child("username").getValue(String.class);
                data.child("users").setValue(users);

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
                                        //Toast.makeText(SelectUser.this, "name: " + name , Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(SelectUser.this, "array" + arr2.get(i) , Toast.LENGTH_SHORT).show();
//                                        if(name.compareTo(temp) == 0) {
                                        if(name.compareTo(temp) == 0) {
                                            dRef.child("threads").push().setValue(thread);
                                        }
                                        //Toast.makeText(SelectUser.this, Integer.toString(arr2.size()) , Toast.LENGTH_SHORT).show();
                                    }
                                }
                             currentUser.child("threads").push().setValue(thread);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

//                Intent main = new Intent(SelectUser.this, Users.class);
                Intent main = new Intent(SelectUser.this, Chat.class);
                main.putExtra("username", username_c);
                main.putExtra("thread_id", thread);

                startActivity(main);
            }
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                threadModel newUser = new threadModel();
//                dataRef.push().child("messages").push().setValue(newUser);
//
//                for(ArrayList<String> userList : GetList()) {
//
//                }
//            });

        profilepics.setImageResource(R.drawable.no_user);
        //profilepics.setImageResource(getResources().getIdentifier(":drawable/stevejobs/stevejobs.jpg", null, null));

    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        CheckedTextView textview = (CheckedTextView)v;
//        textview.setChecked(!textview.isChecked());
//    }
}