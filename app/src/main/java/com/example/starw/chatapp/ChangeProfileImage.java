package com.example.starw.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import static android.app.Activity.RESULT_OK;

public class ChangeProfileImage extends AppCompatActivity {

    ImageView profileImageImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_image);


        profileImageImgView = (ImageView) findViewById(R.id.profileImage);
        Button changeImage = (Button) findViewById(R.id.changeImageBtn);
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

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == RESULT_OK){
            if(reqCode == 1){
                profileImageImgView.setImageURI(data.getData());
            }
        }
    }

    }
