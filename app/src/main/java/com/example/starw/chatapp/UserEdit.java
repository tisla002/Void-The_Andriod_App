package com.example.starw.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserEdit extends AppCompatActivity {
    private TextView myName;
    private ImageView myProfileImage;
    private TextView myUserName;
    private Button changeImg;



    final FirebaseStorage storage = FirebaseStorage.getInstance();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference user_db = database.getReference().child("users");

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String uid = user.getUid();



    String profileImage;

    StorageReference profileImgRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        myName = findViewById(R.id.Name);
        myProfileImage = findViewById(R.id.Image);
        myUserName = findViewById(R.id.Username);
        changeImg =  findViewById(R.id.changeImgBtn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        user_db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getRef().getKey().compareTo(uid) == 0) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String name = user.getDisplayName();
                    profileImage = dataSnapshot.child("profileImg").getValue(String.class);
                    profileImgRef = storage.getReferenceFromUrl(profileImage + "");
                    myName.setText(name);
                    myUserName.setText(username);

                    getImage();
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

        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserEdit.this, ChangeProfileImage.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

        user_db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getRef().getKey().compareTo(uid) == 0) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String name = user.getDisplayName();
                    profileImage = dataSnapshot.child("profileImg").getValue(String.class);
                    profileImgRef = storage.getReferenceFromUrl(profileImage + "");
                    myName.setText(name);
                    myUserName.setText(username);

                    getImage();
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




    private void getImage(){
        GlideApp.with(this)
                .load(profileImgRef)
                .placeholder(R.drawable.no_user)
                .override(128,  128)
                .centerCrop()
                .circleCrop()
                .into(myProfileImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the back button
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}