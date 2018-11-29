package com.finder.valeen.finder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReportActivity extends AppCompatActivity  implements View.OnClickListener{
     private DatabaseReference databaseReference;
     @BindView(R.id.imgData) CircleImageView circleImageView;
     @BindView(R.id.description)TextView txtDescription;
     @BindView(R.id.txtSite)EditText edit;
     @BindView(R.id.review) TextView txtFinder;
     @BindView(R.id.btnPost) Button btnPost;
     @BindView(R.id.toolbar) Toolbar toolbar;
     private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onStart(){
        super.onStart();
        Intent intent=getIntent();
        Glide.with(this)
                .asBitmap()
                .load(intent.getCharSequenceExtra("url"))
                .into(circleImageView);
        txtDescription.setText(intent.getCharSequenceExtra("description"));
        databaseReference=FirebaseDatabase.getInstance().getReference().child((String) intent.getCharSequenceExtra("time")).child("message");
        btnPost.setOnClickListener(this);
        getLastItem();
    }

    public  void getLastItem(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    txtFinder.setText(dataSnapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error",databaseError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnPost)) {
            if(!TextUtils.isEmpty(edit.getText().toString())){
                Log.d("USER",edit.getText().toString());
                databaseReference.setValue(edit.getText().toString());
            }
            else {
                Toast.makeText(this,"Empty Field",Toast.LENGTH_LONG).show();
            }

        }
    }
}
