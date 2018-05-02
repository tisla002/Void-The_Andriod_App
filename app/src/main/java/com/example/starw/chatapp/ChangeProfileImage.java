package com.example.starw.chatapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ChangeProfileImage extends AppCompatActivity {

    ImageView profileImageImgView;
    Button changeImage;

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

        verifyPermissions();
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                profileImageImgView.setImageURI(data.getData());
            }
        }
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



}
