package com.hxt5gh.android.tracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hxt5gh.android.tracker.Models.UserClass;
import com.hxt5gh.android.tracker.R;
import com.hxt5gh.android.tracker.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

      private ActivitySignUpBinding binding;
      private FirebaseAuth mAuth;
      private ProgressDialog progressDialog;
      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("New Account Creating");
        progressDialog.setMessage("Wait for a Moment ...");
        progressDialog.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        binding.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this , SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });




       binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               progressDialog.show();
               String name = binding.idName.getText().toString();
               String email = binding.idEmail.getText().toString();
               String password = binding.idPassword.getText().toString();
               if (TextUtils.isEmpty(name))
               {
                   Toast.makeText(SignUpActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                   progressDialog.dismiss();
                   return;
               }if (TextUtils.isEmpty(email))
               {
                   Toast.makeText(SignUpActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                   progressDialog.dismiss();
                   return;
               }if (TextUtils.isEmpty(password))
               {
                   Toast.makeText(SignUpActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                   progressDialog.dismiss();
                   return;
               }




               mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful())
                       {
                           UserClass userClass = new UserClass();
                           userClass.setName(name);
                           userClass.setEmail(email);
                           userClass.setPassword(password);
                           userClass.setUid(task.getResult().getUser().getUid());

                           mRef.child("Users").child(task.getResult().getUser().getUid()).setValue(userClass);

                       mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful())
                               {
                                   progressDialog.dismiss();
                                   Toast.makeText(SignUpActivity.this, "Register Successfully \nPlease Varify Your Email", Toast.LENGTH_SHORT).show();
                               }
                                    else
                                   {
                                       progressDialog.dismiss();
                                       Toast.makeText(SignUpActivity.this, "error...", Toast.LENGTH_SHORT).show();
                                   }

                           }
                       });

                       }

                   }
               });

           }
       });

    }
}