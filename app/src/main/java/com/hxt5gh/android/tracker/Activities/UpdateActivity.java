package com.hxt5gh.android.tracker.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hxt5gh.android.tracker.MainActivity;
import com.hxt5gh.android.tracker.Models.ClientClass;
import com.hxt5gh.android.tracker.R;
import com.hxt5gh.android.tracker.databinding.ActivityUpdateBinding;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    String pushId;
    String name;
    String mNo;
    String  imageUri;
    private Uri ImageUri = null;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Image....");
        progressDialog.setCancelable(false);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        binding.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        mNo =  intent.getStringExtra("mNo");
        imageUri = intent.getStringExtra("imageUri");
        pushId = intent.getStringExtra("pushID");
        Log.d("TAG", "onCreate: "  + name + " " +mNo + " link "  +imageUri);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(100, 100);
        Glide.with(getApplicationContext()).load(imageUri).apply(requestOptions).placeholder(R.drawable.user).into(binding.profileImage);
        binding.idName.setText(name);
        binding.idMobile.setText(mNo);

        binding.cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        binding.cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent , 555);

            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
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

                                Toast.makeText(UpdateActivity.this, "uri setted", Toast.LENGTH_SHORT).show();
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


        String name1 = binding.idName.getText().toString();
        String mNO1 = binding.idMobile.getText().toString();
        if (name.equals(name1) && mNo.equals(mNO1))
        {
            Toast.makeText(this, "Nothing Changed", Toast.LENGTH_SHORT).show();
            return;
        }





        if (TextUtils.isEmpty(name1))
        {
            Toast.makeText(UpdateActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mNO1))
        {
            Toast.makeText(UpdateActivity.this, "Enter Mobile Number ", Toast.LENGTH_SHORT).show();
            return;
        }




        String id = pushId;
        ClientClass data = new ClientClass();
        data.setName(name1);
        data.setmNumber(mNO1);
     //   data.setPriority(priority);
        if (ImageUri != null)
        {
            data.setImageUrl(ImageUri.toString());
        }
        else
        {
            data.setImageUrl(imageUri);
        }
        data.setPushId(id);



        mRef.child("Data").child(FirebaseAuth.getInstance().getUid()).child(id).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(UpdateActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(UpdateActivity.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();

            }
        });


    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delet User");
        builder.setMessage("Are you sure you want to delete " +name  +" ?");

        // Add "Delete" button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform the deletion here

                final Task<Void> task = mRef.child("Data").child(FirebaseAuth.getInstance().getUid()).child(pushId).removeValue();

                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UpdateActivity.this, "User Removed Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(UpdateActivity.this , MainActivity.class);
                        startActivity(intent1);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateActivity.this, "ERROR..", Toast.LENGTH_SHORT).show();
                    }
                });
                // ...
            }
        });

        // Add "Cancel" button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        // Apply a custom background to the buttons
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                // Apply custom background color to "Delete" button
              //  positiveButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDeleteButton));

                // Apply custom background color to "Cancel" button
               // negativeButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorCancelButton));

                // Apply custom text color to both buttons
                positiveButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                negativeButton.setTextColor(ContextCompat.getColor(getApplicationContext(),  R.color.white));
            }
        });
        dialog.show();
    }
}