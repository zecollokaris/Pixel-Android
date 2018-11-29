package com.finder.valeen.finder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

import Adapters.MessagesAdapter;
import Animations.SimpleAnimation;
import Models.Message;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.newstoolbar) Toolbar newsToolbar;
    @BindView(R.id.messages) RecyclerView recyclerView;
    @BindView(R.id.btnPost)Button btnPost;
    @BindView(R.id.spinner) ProgressBar progressBar;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        btnPost.setOnClickListener(this);
        setSupportActionBar(newsToolbar);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("people");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public  boolean  onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater;
        menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.news_menu,menu);
        return  super.onCreateOptionsMenu(menu);
    }


    @Override
    public  boolean onOptionsItemSelected(MenuItem  menuItem){
        switch (menuItem.getItemId()){
            case  R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent=new Intent(NewsActivity.this,MainActivity.class);
                startActivity(loginIntent);
                break;
        }
        return  true;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnPost)){
            Intent intent=new Intent(NewsActivity.this,PostActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public  void onStart(){
        super.onStart();
        getPeople();
    }


    public  void getPeople(){
        databaseReference.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Message.messages.clear();
                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.getChildrenCount()>=1){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        Log.d("URL",data.child("url").getValue().toString());
                       Message message=new Message(data.child("url").getValue().toString(),data.child("message").getValue().toString(),data.child("date").getValue().toString(),
                               Long.parseLong(data.child("time").getValue().toString()));
                       Message.messages.add(message);
                    }
                    Collections.reverse(Message.messages);
                    MessagesAdapter messagesAdapter=new MessagesAdapter(NewsActivity.this,Message.messages,getSupportFragmentManager());
                    recyclerView.setAdapter(messagesAdapter);
                    SimpleAnimation simpleAnimation=new SimpleAnimation(messagesAdapter);
                    ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleAnimation);
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Message.messages.clear();
                Log.d("DATA",databaseError.getMessage());
            }
        });
    }
}
