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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ChangeProfileImage extends AppCompatActivity {

    ImageView profileImageImgView;
    Button changeImage, upload;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://void-app-5369d.appspot.com");
    Uri filePath;
    ProgressDialog pd;

    String imageURL = "https://firebasestorage.googleapis.com/v0/b/void-app-5369d.appspot.com/o/profile_images%2Fno_user.png?alt=media&token=7480d07a-e9cd-41d5-ad7e-63e95624a66e";

    private static final String TAG = "ChangeImageActivity";
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_image);

        profileImageImgView = (ImageView) findViewById(R.id.profileImage);
        Picasso.get().load(imageURL).into(profileImageImgView);
        changeImage = (Button) findViewById(R.id.changeImageBtn);
        upload = (Button) findViewById(R.id.uploadbtn);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        verifyPermissions();
        uploadImg();



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
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null) {
                    pd.show();

                    StorageReference childRef = storageRef.child("Profile_Img").child("profile.jpg");

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(ChangeProfileImage.this, "Upload successful", Toast.LENGTH_SHORT).show();
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



}
