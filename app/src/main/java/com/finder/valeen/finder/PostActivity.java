package com.finder.valeen.finder;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import Models.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.postImage) ImageView imageView;
    @BindView(R.id.postData) Button btnPost;
    @BindView(R.id.txtDescription)TextView txtMessage;
    ProgressDialog upload;
    final  private  int REQUEST_CODE=1;
    private boolean selected=false;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        imageView.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        firebaseStorage=FirebaseStorage.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        initProgress();
    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override

    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        requestPermison();
        if(requestCode==REQUEST_CODE && data!=null){
            Uri selectedImage=data.getData();
            Glide.with(this).asBitmap().load(selectedImage).into(imageView);
            selected=true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void  requestPermison(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Log.e("TRUE","TRUE");
            }
            else{
                Log.e("TRUE","FALSE");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(imageView)){
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_CODE);
        }
        else if(v.equals(btnPost)){
            if(selected && !TextUtils.isEmpty(txtMessage.getText().toString())){
                upload();
            }
            else{
                Toast.makeText(this,"Upload Failed",Toast.LENGTH_LONG).show();
            }
        }
    }
    private  void initProgress(){
        upload=new ProgressDialog(this);
        upload.setMessage("Uploading Image");
        upload.setCancelable(false);
    }



    public  void upload(){
        final StorageReference storageReference;
        if(selected){
            upload.show();
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] bytes=baos.toByteArray();
            final String date=new Date().toLocaleString();
            storageReference=firebaseStorage.getReference(date);
            UploadTask uploadTask=storageReference.putBytes(bytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    upload.dismiss();
                    Log.d("Data",e.getMessage());
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    upload.dismiss();
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Log.d("Processing","DATA");
                            final Uri uri=task.getResult();
                            if(!txtMessage.getText().toString().equals("")){
                                Long date=new Date().getTime();
                                databaseReference.child("people").child(Long.toString(date)).setValue(new Message(uri.toString(),txtMessage.getText().toString()
                                        ,new Date().toLocaleString(),date)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Log.d("Done","Is Done");
                                                }
                                                else{
                                                    Log.e("DBERROR",task.getException().getMessage());
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            });
        }
        else{
            Toast.makeText(PostActivity.this,"Upload Photo",Toast.LENGTH_SHORT).show();
        }
    }

}
