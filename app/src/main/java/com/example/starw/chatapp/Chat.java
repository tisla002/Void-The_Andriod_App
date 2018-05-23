package com.example.starw.chatapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Chat extends AppCompatActivity {
    LinearLayout layout;
    ImageView sendButton;
    ImageView cameraButton;
    ImageView galleyButton;
    EditText messageArea;
    ScrollView scrollView;
    ImageView online;
    TextView usersTyping;
    Uri filePath;
    Random rand = new Random();


    private String username;

    String profileImage;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://void-app-5369d.appspot.com");

    String thread_id_ref;
    DatabaseReference dataRefPic;
    DatabaseReference users;

    Uri imageUri;

    String userIsOnline = "false";

    String sends;

    private static final String TAG = "ChatActivity";
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    ArrayList<String> typers;

    ImageView userOnline;

    String ONLINE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        layout = findViewById(R.id.layout1);
        sendButton = findViewById(R.id.sendButton);
        cameraButton = findViewById(R.id.cameraButton);
        galleyButton = findViewById(R.id.galleyButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        online = findViewById(R.id.online);
        usersTyping = findViewById(R.id.usersTyping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
        final Intent intent = getIntent();
        final String thread_id = intent.getStringExtra("thread_id");
        thread_id_ref = thread_id;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dataRef = database.getReference()
                .child("threads")
                .child(thread_id)
                .child("messages");
        final DatabaseReference typing = database.getReference()
                .child("threads")
                .child(thread_id)
                .child("typing");

        typing.onDisconnect().removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        typing.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                typers = new ArrayList<>();
                                for(DataSnapshot data: dataSnapshot.getChildren()) {
                                    typers.add(data.getValue(String.class));
                                }
                                if(typers.contains(username)) {
                                    typers.remove(username);
                                    typing.setValue(username);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });


        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    typers = new ArrayList<String>();
                    if(typing.getClass() == null) {
                        Log.d(TAG, "onTextChanged: GetClass returned null");
                    } else {
                        Log.d(TAG, "onTextChanged: not null");
                    }
                    typing.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onTextChanged: called");
                            if(!dataSnapshot.exists()) {
                                typers.add(username);
                                typing.setValue(typers);
                            }
                            for(DataSnapshot data: dataSnapshot.getChildren()) {
                                typers.add(data.getValue(String.class));
                            }
                            if(!typers.contains(username)) {
                                typers.add(username);
                                typing.setValue(typers);
                            }
                            Log.d(TAG, "onTextChanged: called" + typers);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Log.d(TAG, "onTextChanged: " + typing);
                    if(typing == null) {
                        typers.add(username);
                        typing.setValue(typers);
                    }
                } else {
                    // Set to false
                    typers = new ArrayList<String>();
                    typing.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()) {
                                typers.add(data.getValue(String.class));
                            }
                            if(typers.contains(username)) {
                                typers.remove(username);
                                typing.setValue(typers);
                            }
                            Log.d(TAG, "onTextChanged: called" + typers);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dataRefPic = dataRef;

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        users = database.getReference()
                .child("users");

//        users.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot data: dataSnapshot.getChildren() ){
//                    String x = data.child("username").getValue(String.class);
//
//                    userIsOnline = data.child("online").getValue(String.class);
//                    Toast.makeText(Chat.this, userIsOnline, Toast.LENGTH_SHORT).show();
//
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getRef().getKey().compareTo(uid) == 0) {
                    username = dataSnapshot.child("username").getValue(String.class);
                    Log.d("STATE", username);
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

        typing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> typersTo = new ArrayList<>();
                for(DataSnapshot data: dataSnapshot.getChildren()) {
                    if(!(data.getValue(String.class).compareTo(username) == 0)) {
                        typersTo.add(data.getValue(String.class));
                    }
                }
                if(!typersTo.isEmpty()) {
                    String fullList = android.text.TextUtils.join(", ", typersTo);
                    String typerList = fullList.substring(0,
                            Math.min(fullList.length(), 40)) + " is typing...";
                    usersTyping.setText(typerList);
                } else {
                    usersTyping.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    UserandTextModel pushUser = new UserandTextModel(username, messageText);
                    dataRef.push().setValue(pushUser);
                    messageArea.setText("");
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPermission()){
                    openCamera();
                }
                else{
                    verifyPermissions();
                }
            }
        });

        galleyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPermission()){
                    SelectImage();
                }
                else{
                    verifyPermissions();
                }


            }
        });

        dataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String sender = dataSnapshot.child("user").getValue(String.class);
                sends = dataSnapshot.child("user").getValue(String.class);

                Log.d("USERPIC", "Sender: " + sender);
                if(dataSnapshot.child("type").getValue(String.class).compareTo("text") == 0) {
                    String x = dataSnapshot.child("text").getValue(String.class);


                    if (sender.compareTo(username) == 0) {
                        addMessageBox(sender, x, 1);
                    } else {
                        addMessageBox(sender, x, 2);
                    }
                } else if(dataSnapshot.child("type").getValue(String.class).compareTo("Picture") == 0) {
                    String x = dataSnapshot.child("Pic").getValue(String.class);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference img = storage.getReferenceFromUrl(x);

                    if (sender.compareTo(username) == 0) {
                        addPicBox(sender, img, 1);
                    } else {
                        addPicBox(sender, img, 2);
                    }
                } else {
                    Log.e("TYPE ERROR:", "Firebase threw object of no known type");
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.child("type").getValue(String.class).compareTo("Picture") == 0) {
                    String sender = dataSnapshot.child("user").getValue(String.class);

                    String x = dataSnapshot.child("Pic").getValue(String.class);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference img = storage.getReferenceFromUrl(x);

                    LayoutInflater inflater = LayoutInflater.from(Chat.this);

                    RelativeLayout stuff1 = (RelativeLayout) inflater.inflate(R.layout.my_picture, null, true);
                    ImageView sentPic = stuff1.findViewById(R.id.Spicture);
                    getImage2(img, sentPic);

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });




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

    public void addMessageBox(final String user, String message, int type) {

        LayoutInflater inflater = LayoutInflater.from(Chat.this);

        FirebaseDatabase profileImg = FirebaseDatabase.getInstance();
        DatabaseReference profileImgRef = profileImg.getReference().child("users");

        RelativeLayout stuff = (RelativeLayout) inflater.inflate(R.layout.their_message, null, true);
        TextView messageBody = stuff.findViewById(R.id.message_body);
        TextView userName = stuff.findViewById(R.id.name);
        userOnline = stuff.findViewById(R.id.online);
        final ImageView userPic = stuff.findViewById(R.id.avatar);
        messageBody.setText(message);
        userName.setText(user);
        userPic.setImageResource(R.drawable.no_user);

        if(type == 2){
            isOnline(user, userOnline);
        }

        profileImgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String name = data.child("username").getValue(String.class);

                    if(name.compareTo(user) == 0){
                        profileImage = data.child("profileImg").getValue(String.class);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference img = storage.getReferenceFromUrl(profileImage);

                        getImage(img, userPic);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RelativeLayout stuff1 = (RelativeLayout) inflater.inflate(R.layout.my_message, null, true);
        TextView messageBody1 = stuff1.findViewById(R.id.message_body);
        messageBody1.setText(message);


        if(type == 1){
            layout.addView(stuff1);
        } else{
            layout.addView(stuff);
        }

        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void addPicBox(final String user, StorageReference pic, final int type) {

        LayoutInflater inflater = LayoutInflater.from(Chat.this);


        FirebaseDatabase profileImg = FirebaseDatabase.getInstance();
        DatabaseReference profileImgRef = profileImg.getReference().child("users");

        RelativeLayout stuff = (RelativeLayout) inflater.inflate(R.layout.their_picture, null, true);
        ImageView recievedPic = stuff.findViewById(R.id.picture);
        TextView userName = stuff.findViewById(R.id.name);
        final ImageView userPic = stuff.findViewById(R.id.avatar);
        userOnline = stuff.findViewById(R.id.online);
        userName.setText(user);
        userPic.setImageResource(R.drawable.no_user);
        getImage2(pic, recievedPic);

        if(type == 2){
            isOnline(user, userOnline);
        }

        profileImgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(type == 2) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String name = data.child("username").getValue(String.class);

                        if (name.compareTo(user) == 0) {
                            profileImage = data.child("profileImg").getValue(String.class);
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference img = storage.getReferenceFromUrl(profileImage);

                            Log.d("USERPIC", img.toString());
                            Log.d("USERPIC", user);

                            getImage(img, userPic);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RelativeLayout stuff1 = (RelativeLayout) inflater.inflate(R.layout.my_picture, null, true);
        ImageView sentPic = stuff1.findViewById(R.id.Spicture);
        getImage2(pic, sentPic);


        if(type == 1){
            layout.addView(stuff1);
        } else{
            layout.addView(stuff);
        }

        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void getImage(StorageReference img, ImageView userPic){
        GlideApp.with(getApplicationContext())
                .load(img)
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.no_user)
                .into(userPic);
    }

    private void getImage2(StorageReference img, ImageView userPic){
        GlideApp.with(getApplicationContext())
                .load(img)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()
                .into(userPic);
    }

    private void verifyPermissions() {
        Log.d(TAG, "verifyPermissions: asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED){

        } else {
            ActivityCompat.requestPermissions(Chat.this,
                    permissions,
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    private boolean hasPermission(){
        Log.d(TAG, "verifyPermissions: asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            return false;
        }
    }


    public void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 1);
    }

    public void openCamera() {
        final int n = rand.nextInt(9999) + 1;
        String fileName = n + "cameraImage.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (reqCode == 1 && resCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            UploadImage();
        }
        if (reqCode == REQUEST_IMAGE_CAPTURE && resCode == RESULT_OK) {
            filePath = imageUri;
            UploadImage();
        }
    }

    public void UploadImage(){
        final int n = rand.nextInt(9999) + 1;
        if(filePath != null) {
            final StorageReference childRef = storageRef.child("thread_images").child(thread_id_ref).child(n+"image.jpg");

            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Chat.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    Log.d("USERTHR2: ", "DID 13");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Chat.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });

            UserandPicModel pushUser = new UserandPicModel(username, childRef.toString());
            dataRefPic.push().setValue(pushUser);
        }
        else {
            Toast.makeText(Chat.this, "Select an image", Toast.LENGTH_SHORT).show();
        }

    }

    public void isOnline(final String user, final ImageView img){

        FirebaseDatabase profileImg = FirebaseDatabase.getInstance();
        DatabaseReference profileImgRef = profileImg.getReference().child("users");

        profileImgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren() ){
                    String x = data.child("username").getValue(String.class);
                    if(x.compareTo(user) == 0){
                        dataAccessor(data, img);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void dataAccessor(DataSnapshot data, ImageView img){
        ONLINE = data.child("online").getValue(String.class);
        String comp = "true";

        if(ONLINE.compareTo(comp) == 0){
            img.setColorFilter(Color.rgb(0, 255, 0));
        }else {
            img.setColorFilter(Color.rgb(255, 0, 0));
        }
    }

}