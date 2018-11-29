package Fragment;

import android.app.Notification;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.finder.valeen.finder.MainActivity;
import com.finder.valeen.finder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpFragment extends DialogFragment implements  View.OnClickListener {
    private ProgressDialog loading;
    TextView txtUsername;
    TextView txtPassword;
    TextView txtConfirm;
    Button btnSignUp;
    Button btnCancel;
    FirebaseAuth mAuth;
    private DatabaseReference notification;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View view=inflater.inflate(R.layout.signup_layout,viewGroup,false);
        txtUsername=view.findViewById(R.id.txtName);
        txtPassword=view.findViewById(R.id.txtPass);
        txtConfirm=view.findViewById(R.id.txtConfirm);
        btnSignUp=view.findViewById(R.id.btnSignUp);
        btnCancel=view.findViewById(R.id.btnCancel);
        getDialog().setTitle("SIGN UP");
        createProgress();
        notification= FirebaseDatabase.getInstance().getReference().child("messages");
        mAuth=FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        return  view;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnSignUp)){
            String username=txtUsername.getText().toString();
            String password=txtPassword.getText().toString();
            String confirm=txtConfirm.getText().toString();
            if(TextUtils.isEmpty(confirm) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)){
                Toast.makeText(getContext(),"Empty Fields",Toast.LENGTH_LONG).show();
            }
            else{
                if(password.equals(confirm)){
                     createUser(username,password);
                }
                else{
                    Toast.makeText(getContext(),"Incorrect Password Confirmation",Toast.LENGTH_LONG).show();
                }
            }
        }
        else if(v.equals(btnCancel)){
            getDialog().cancel();
        }
    }

    private void createUser(String username,String password){
        loading.show();
        mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading.dismiss();
                if(task.isSuccessful()){
//                    createNotification();
                    Toast.makeText(getContext(),"Successful",Toast.LENGTH_LONG).show();
                    getDialog().cancel();
                }
                else{
                    Toast.makeText(getContext(),"Authentication Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createProgress(){
        loading=new ProgressDialog(getContext());
        loading.setTitle("Creating Account");
        loading.setMessage("Loading ...");
        loading.setCancelable(true);
    }
}
