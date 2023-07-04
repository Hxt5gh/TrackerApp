package com.hxt5gh.android.tracker.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hxt5gh.android.tracker.MainActivity;
import com.hxt5gh.android.tracker.Models.ClientClass;
import com.hxt5gh.android.tracker.R;
import com.hxt5gh.android.tracker.databinding.ActivityInsertBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class InsertActivity extends AppCompatActivity {

    private ActivityInsertBinding binding;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private Uri ImageUri = null;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Image....");
        progressDialog.setCancelable(false);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        binding.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InsertActivity.this, "save", Toast.LENGTH_SHORT).show();
            }
        });

        binding.cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               startActivityForResult(intent , 555);

            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSave();
            }
        });

    }



    private ByteArrayOutputStream baos;
    Bitmap bitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 555)
        {
            if (data != null)
            {

                progressDialog.show();
                bitmap = (Bitmap) data.getExtras().get("data");



                //compress
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] fileInBytes = baos.toByteArray();



                Calendar calendar = Calendar.getInstance();
                storageReference.child("Images").child(calendar.getTimeInMillis() +"").putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.child("Images").child(calendar.getTimeInMillis() +"").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Toast.makeText(InsertActivity.this, "uri setted", Toast.LENGTH_SHORT).show();
                               ImageUri = uri;
                               binding.profileImage.setImageBitmap(bitmap);
                               progressDialog.dismiss();

                            }
                        });

                    }
                });
            }

        }
    }

    private void dataSave() {
        String name = binding.idName.getText().toString();
        String mNO = binding.idNumber.getText().toString();
        int priority = 0;

        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(InsertActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mNO))
        {
            Toast.makeText(InsertActivity.this, "Enter Mobile Number ", Toast.LENGTH_SHORT).show();
            return;
        }




        String id = mRef.push().getKey();
        ClientClass data = new ClientClass();
        data.setName(name);
        data.setmNumber(mNO);
        data.setPriority(priority);
        if (ImageUri != null)
        {
        data.setImageUrl(ImageUri.toString());
        }
        else
        {
        data.setImageUrl("");
        }
        data.setPushId(id);



        mRef.child("Data").child(FirebaseAuth.getInstance().getUid()).child(id).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(InsertActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(InsertActivity.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();

            }
        });


    }

}