package com.example.starw.chatapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class ChangeProfileImage extends AppCompatActivity {

    ImageView profileImageImgView;
    Button changeImage, upload;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://void-app-5369d.appspot.com");
    StorageReference profileImgRef = storage.getReferenceFromUrl("gs://void-app-5369d.appspot.com/profile_Img/no_user.png");
    Uri filePath;
    ProgressDialog pd;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference user_db = database.getReference().child("users");

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String uid = user.getUid();
    String image_loc;
    String profileImage;
    Random rand = new Random();

    private static final String TAG = "ChangeImageActivity";
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_image);

        user_db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getRef().getKey().compareTo(uid) == 0) {
                    image_loc = dataSnapshot.child("image_loc").getValue(String.class);
                    profileImage = dataSnapshot.child("profileImg").getValue(String.class);

                    storageRef = storage.getReferenceFromUrl(image_loc);
                    profileImgRef = storage.getReferenceFromUrl(profileImage);
                    getmage();
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

        changeImage = findViewById(R.id.changeImageBtn);
        upload = findViewById(R.id.uploadbtn);
        profileImageImgView = findViewById(R.id.profileImage);

        //profileImageImgView.setImageResource(R.drawable.no_user);
        //Picasso.get().load(imageURL).into(profileImageImgView);




        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        verifyPermissions();
        uploadImg();

        getmage();


    }

    private void verifyPermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED)
        {
            setNewImage();
        }else{
            ActivityCompat.requestPermissions(ChangeProfileImage.this,
                    permissions,
                    REQUEST_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    private void setNewImage(){
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 1);

            }
        });

    }
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (reqCode == 1 && resCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageImgView.setImageURI(data.getData());
            filePath = data.getData();
        }
        try {
            //getting image from gallery
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

            //Setting image to ImageView
            profileImageImgView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImg(){
        final int n = rand.nextInt(9999) + 1;
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null) {
                    pd.show();

                    final StorageReference childRef = storageRef.child(n + "profile.jpg");

                    final String newProfilImg = image_loc + "/" + n + "profile.jpg";
                    final String key = user_db.child(uid).child("profileImg").getKey();

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(ChangeProfileImage.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            Map<String, Object> update = new HashMap<>();
                            update.put("/" + uid + "/" + key, newProfilImg);
                            user_db.updateChildren(update);
                            profileImgRef = childRef;


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(ChangeProfileImage.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(ChangeProfileImage.this, "Select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getmage(){
        GlideApp.with(this)
                .load(profileImgRef)
                .override(128,  128)
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.no_user)
                .into(profileImageImgView);;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //This is the back button
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }




}