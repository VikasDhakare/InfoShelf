package com.card.infoshelf.loginandsignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.FormActivity;
import com.card.infoshelf.MainActivity;
import com.card.infoshelf.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    private EditText et_email , et_pass;
    private TextView tv_forgot , tv_create_account;
    private TextView btn_log_in;
    private FirebaseAuth mAuth;
    private View parent_layout;
    private ProgressDialog progressDialog;
    private String CurrentUserId;
    DatabaseReference checkDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        et_email = findViewById(R.id.et_email);
        et_pass  = findViewById(R.id.et_password);
        tv_forgot = findViewById(R.id.tv_forgot);
        tv_create_account = findViewById(R.id.tv_create_account);
        btn_log_in = findViewById(R.id.btn_log_in);

        progressDialog = new ProgressDialog(LogInActivity.this);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);

        checkDetails = FirebaseDatabase.getInstance().getReference("UserDetails");

        mAuth = FirebaseAuth.getInstance();

        tv_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this , RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String pass = et_pass.getText().toString();

                if (email.isEmpty() && pass.isEmpty())
                {
                    et_email.setError("Enter Email");
                    et_pass.setError("Enter password");
                }
                else if (email.isEmpty() || pass.isEmpty())
                {
                    if (email.isEmpty())
                    {
                        et_email.setError("Enter Email");
                    }
                    if (pass.isEmpty())
                    {
                        et_pass.setError("Enter Password");
                    }
                }
                else
                {
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);

                    ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
                    Sprite doubleBounce = new ThreeBounce();
                    progressBar.setIndeterminateDrawable(doubleBounce);

                    mAuth.signInWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user.isEmailVerified()){

                                    CurrentUserId = mAuth.getCurrentUser().getUid();

                                    checkDetails.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(CurrentUserId)){
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(LogInActivity.this , MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(LogInActivity.this , FormActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else {
                                    user.sendEmailVerification();
                                    progressDialog.dismiss();
                                    Toast.makeText(LogInActivity.this, "check Your Email To Verified", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else
                            {
                                String message = task.getException().toString();
                                progressDialog.dismiss();
                                Toast.makeText(LogInActivity.this, "Error ! "+message, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });

        tv_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText resetmail = new EditText(v.getContext());
                AlertDialog.Builder passwordDialog = new AlertDialog.Builder(v.getContext());
                passwordDialog.setTitle("Reset Password");
                passwordDialog.setMessage("Enter Your Mail To Received Reset Link");
                passwordDialog.setView(resetmail);

                passwordDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetmail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LogInActivity.this, "Reset Link Sent To Your Mail.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });

                passwordDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordDialog.create().show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser = mAuth.getCurrentUser();

        if (currentuser != null)
        {
            if (currentuser.isEmailVerified()){

                checkDetails.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(currentuser.getUid())){
                            Intent intent = new Intent(LogInActivity.this , MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(LogInActivity.this , FormActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

        }
        else
        {

        }

    }
}