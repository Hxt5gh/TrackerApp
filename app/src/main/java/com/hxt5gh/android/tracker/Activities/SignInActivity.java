package com.hxt5gh.android.tracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hxt5gh.android.tracker.MainActivity;
import com.hxt5gh.android.tracker.Models.UserClass;
import com.hxt5gh.android.tracker.R;
import com.hxt5gh.android.tracker.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 99 ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference  mRef;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null)
        {
            if (mAuth.getCurrentUser().isEmailVerified())
            {
            Intent intent = new Intent(SignInActivity.this , MainActivity.class);
            startActivity(intent);
            finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging To your Account");
        progressDialog.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this , SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailSignIn();
            }
        });

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    //email sign in
    private void emailSignIn() {
        progressDialog.show();
        String email = binding.idEmail.getText().toString();
        String password = binding.idPassword.getText().toString();
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(SignInActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }if (TextUtils.isEmpty(password))
        {
            Toast.makeText(SignInActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }




        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    if (mAuth.getCurrentUser().isEmailVerified())
                    {

                        progressDialog.dismiss();
                        Intent intent = new Intent(SignInActivity.this , MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "Please varify your Email", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "Fail To Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    //google sign in
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
          progressDialog.show();
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);


                FirebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                //  updateUI(null);
            }

        }
    }

    private void FirebaseAuthWithGoogle(String idToken)
    {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken , null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful())
                        {
                            FirebaseUser user = task.getResult().getUser();
                            UserClass userClass = new UserClass();
                            userClass.setName(user.getDisplayName());
                            userClass.setEmail(user.getEmail());
                            userClass.setUid(user.getUid());

                            mRef.child("Users").child(user.getUid()).setValue(userClass);

                            progressDialog.dismiss();
                            Intent intent = new Intent(SignInActivity.this , MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SignInActivity.this, "ffff", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }



}