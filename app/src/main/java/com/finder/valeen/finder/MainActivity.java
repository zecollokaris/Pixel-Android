package com.finder.valeen.finder;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Fragment.SignUpFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth=null;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog loading;
    @BindView(R.id.anim1) LinearLayout anim1;
    @BindView(R.id.anim2) LinearLayout anim2;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnSignUp) Button btnSignUp;
    @BindView(R.id.txtUsername) TextView txtUsername;
    @BindView(R.id.txtPassword) TextView txtPassword;
    Animation downtoup;
    Animation uptodown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        uptodown= AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup=AnimationUtils.loadAnimation(this,R.anim.downtoup);
        anim1.setAnimation(uptodown);
        anim2.setAnimation(downtoup);
        mAuth=FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        createAuthListener();
        createProgress();
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public  void onStop(){
        super.onStop();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAuthListener(){
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    Intent newsIntent=new Intent(MainActivity.this,NewsActivity.class);
                    startActivity(newsIntent);
                }
            }
        };
    }

    private void createLogin(String username,String password){
        loading.show();
        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading.dismiss();
                if(task.isSuccessful()){
                    Intent newsIntent=new Intent(MainActivity.this,NewsActivity.class);
                    startActivity(newsIntent);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v.equals(btnSignUp)){
            SignUpFragment signUpFragment=new SignUpFragment();
            signUpFragment.show(getSupportFragmentManager(),"SIGN UP");
        } else if (v.equals(btnLogin)) {
            if(!TextUtils.isEmpty(txtPassword.getText().toString()) && !TextUtils.isEmpty(txtUsername.getText().toString())){
                 createLogin(txtUsername.getText().toString(),txtPassword.getText().toString());
            }
            else{
                Toast.makeText(this,"Authentication Failed",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createProgress(){
        loading=new ProgressDialog(this);
        loading.setTitle("Authenticating User");
        loading.setMessage("Loading ...");
        loading.setCancelable(true);
    }
}
