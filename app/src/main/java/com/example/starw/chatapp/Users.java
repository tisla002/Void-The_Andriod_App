package com.example.starw.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Users extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;
    FloatingActionButton add;
    SwipeRefreshLayout refresh;

    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference user_threads = database.getReference().child("users").child(uid);
    final DatabaseReference available_threads = database.getReference().child("threads");

    private ArrayList<String> threads = new ArrayList<>();
    private ArrayList<String> thread_names = new ArrayList<>();
    private String username;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersList = findViewById(R.id.usersList);
        noUsersText = findViewById(R.id.noUsersText);
        add = findViewById(R.id.fabButton);
        refresh = findViewById(R.id.swiperefresh);

        pd = new ProgressDialog(Users.this);
        pd.setMessage("Loading...");
        pd.show();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(new Intent(Users.this, Users.class));
                finish();
            }
        });

        // Always get the list of reference hashes for threads the current user is a part of.
        user_threads.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().compareTo("username") == 0) {
                    username = dataSnapshot.getValue(String.class);
                }

                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    threads.add(data.getValue(String.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    if (!threads.contains(data.getValue(String.class))) {
                        threads.add(data.getValue(String.class));
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Once the list of thread hashes has been acquired, then generate the names for them.
        available_threads.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                buildThreadNames(dataSnapshot);
                doOnSuccess(thread_names);
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

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chat = new Intent(Users.this, Chat.class);
                chat.putExtra("thread_id", threads.get(position));
                chat.putExtra("thread_name", thread_names.get(position));

                startActivity(chat);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Select = new Intent(Users.this, NewSelectUser.class);
                startActivity(Select);


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.profileMenu:
                startActivity(new Intent(Users.this, UserEdit.class));
                break;

            case R.id.signoutMenu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(Users.this, Login.class));
                                finish();
                            }
                        });

                user_threads.child("online").setValue("false");//Twice <3
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void doOnSuccess(ArrayList<String> t) {
        if (t.size() == 0) {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);

            usersList.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    t));
        }

        pd.dismiss();
    }

    public void buildThreadNames(DataSnapshot s) {
        for (DataSnapshot thread: s.getChildren()) {
            if (threads.contains(thread.getRef().getParent().getKey())) {
                ArrayList<String> usernames = new ArrayList<>();

                for (DataSnapshot snap: thread.getChildren()) {
                    if (snap.getRef().getParent().getKey().compareTo("users") == 0) {
                        // Don't add current user's username to usernames list.
                        if (snap.getValue(String.class).compareTo(username) != 0) {
                            usernames.add(snap.getValue(String.class));
                        }
                    }
                }

                if (usernames.size() > 0) {
                    // Join the list into a comma separated string and cut to 50 characters.
                    String fullList = android.text.TextUtils.join(", ", usernames);
                    String uiList = fullList.substring(0, Math.min(fullList.length(), 45));

                    if (fullList.length() > 50) {
                        // For long names concat an ellipsis at the end of the uiList.
                        uiList = uiList.concat(" ...");
                    }

                    thread_names.add(uiList);
                }
            }
        }
    }
}