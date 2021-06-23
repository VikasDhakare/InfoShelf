package com.card.infoshelf.loginandsignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_username , et_email , et_pass , et_con_pass;
    private TextView  btn_create;
    private TextView tv_have_account;
    private DatabaseReference regRef;
    private String CurrentUserId, token;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = findViewById(R.id.et_userName);
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_password);
        et_con_pass = findViewById(R.id.et_confirmPassword);
        btn_create = findViewById(R.id.btn_create);
        tv_have_account = findViewById(R.id.tv_have_account);

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        regRef = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            token = Objects.requireNonNull(task.getResult()).getToken();

                        }

                    }
                });

        tv_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this , LogInActivity.class);
                startActivity(intent);
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u_name = et_username.getText().toString();
                String u_email = et_email.getText().toString();
                String u_pass = et_pass.getText().toString();
                String u_c_pass = et_con_pass.getText().toString();

                if (u_name.isEmpty() && u_email.isEmpty() && u_pass.isEmpty() && u_c_pass.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please Fill All Credentials", Toast.LENGTH_SHORT).show();
                }
                else if (u_name.isEmpty() || u_email.isEmpty() || u_pass.isEmpty() || u_c_pass.isEmpty() )
                {
                    if (u_name.isEmpty())
                    {
                        et_username.setError("Enter UserName");
                    }
                    if (u_email.isEmpty())
                    {
                        et_email.setError("Enter Email");
                    }
                    if (u_pass.isEmpty())
                    {
                        et_pass.setError("Enter Password");
                    }
                    if (u_c_pass.isEmpty())
                    {
                        et_con_pass.setError("Enter Confirm Password");
                    }

                }
                else if (!u_pass.equals(u_c_pass))
                {
                    et_con_pass.setError("Password Not Matching");
                }
                else
                {

                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);

                    ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
                    Sprite doubleBounce = new ThreeBounce();
                    progressBar.setIndeterminateDrawable(doubleBounce);

                    mAuth.createUserWithEmailAndPassword(u_email, u_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                CurrentUserId = mAuth.getCurrentUser().getUid();

                                HashMap<Object, String> haspmap = new HashMap<>();
                                haspmap.put("userName", u_name);
                                haspmap.put("userEmail", u_email);
                                haspmap.put("userId", CurrentUserId);
                                haspmap.put("token", token);

                                regRef.child(CurrentUserId).setValue(haspmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
                                        startActivity(intent);
                                        finish();
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                            else {
                                progressDialog.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error ! " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}