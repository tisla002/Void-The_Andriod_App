package com.example.starw.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class NewSelectUser extends AppCompatActivity {
    LinearLayout layout;
    ScrollView scrollView;
    FloatingActionButton floatingActionButton;

    String profileImage;
    ArrayList<String> listofUsers = new ArrayList<>();
    String currentUser;
    String thread;

    FirebaseDatabase databaseUsers = FirebaseDatabase.getInstance();
    DatabaseReference databaseUsersRef = databaseUsers.getReference().child("users");

    DatabaseReference dataRef = databaseUsers.getReference().child("threads");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user_select_activity);

        layout = findViewById(R.id.layout2);
        scrollView = findViewById(R.id.scrollView2);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();



        databaseUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.child(uid).child("username").getValue(String.class);

                listofUsers.add(currentUser);

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String name = data.child("username").getValue(String.class);
                    profileImage = data.child("profileImg").getValue(String.class);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference img = storage.getReferenceFromUrl(profileImage);

                    createUsers(name, img);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void createUsers(final String name, StorageReference img){
        LayoutInflater inflater = LayoutInflater.from(NewSelectUser.this);

        RelativeLayout stuff = (RelativeLayout) inflater.inflate(R.layout.new_user_select, null, true);
        TextView userName = stuff.findViewById(R.id.name1);
        ImageView userPic = stuff.findViewById(R.id.avatar1);
        final CheckBox check = stuff.findViewById(R.id.checkBox);
        userPic.setImageResource(R.drawable.no_user);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check.isChecked() == true){
                    //Toast.makeText(NewSelectUser.this, name, Toast.LENGTH_SHORT).show();
                    if(!listofUsers.contains(name)){
                        listofUsers.add(name);
                    }

                }else{
                    if(listofUsers.contains(name)){
                        removeFromArrayList(name);
                    }
                }

                if(!listofUsers.isEmpty()){
                    onButton();
                }

            }
        });

        userName.setText(name);
        getImage(img, userPic);

//        Toast.makeText(NewSelectUser.this, listofUsers+"", Toast.LENGTH_SHORT).show();
        layout.addView(stuff);
        scrollView.fullScroll(View.FOCUS_DOWN);

    }

    public void removeFromArrayList(String name){
        int idx = 0;

        while (idx < listofUsers.size())
        {
            if(listofUsers.get(idx) == name)
            {
                // Remove item
                listofUsers.remove(idx);
            }
            else
            {
                ++idx;
            }
        }
    }

    public void onButton(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(NewSelectUser.this, listofUsers+"", Toast.LENGTH_SHORT).show();
                DatabaseReference data = dataRef.push(); //threads->key
                thread = data.getKey();//key

                //listofUsers is an arraylist of all the users that are selected
                databaseUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {//users
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){ //users->key
                            String user = data.child("username").getValue(String.class);
                            DatabaseReference dRef = data.getRef();

                            if(listofUsers.contains(user)){
                                dRef.child("threads").push().setValue(thread);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                data.child("users").setValue(listofUsers);
                Intent main = new Intent(NewSelectUser.this, Chat.class);
                main.putExtra("username", currentUser);
                main.putExtra("thread_id", thread);

                startActivity(main);
            }
        });
    }

    private void getImage(StorageReference img, ImageView userPic){
        GlideApp.with(getApplicationContext())
                .load(img)
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.no_user)
                .into(userPic);
    }
}