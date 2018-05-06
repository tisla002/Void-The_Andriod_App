package com.example.starw.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Users extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://void-app-5369d.firebaseio.com/");
    private final DatabaseReference thread_db = database.getReference();

    private ArrayList<String> threads = new ArrayList<>();
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);

        pd = new ProgressDialog(Users.this);
        pd.setMessage("Loading...");
        pd.show();

        thread_db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    if (data.getRef().getParent().getKey().compareTo("threads") == 0) {
                        threads.add(data.getKey());
                    }
                }

                doOnSuccess(threads);
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
                chat.putExtra("username", username);

                startActivity(chat);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.profileMenu:
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Users.this, UserEdit.class));
                break;

            case R.id.signoutMenu:
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Users.this, Login.class));
                break;
        }

        return true;
    }

    public void doOnSuccess(ArrayList<String> t) {
        if (t.size() == 0) {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);

            usersList.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    t));
        }

        pd.dismiss();
    }
}