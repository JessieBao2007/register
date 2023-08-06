package com.firstapp.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class other extends AppCompatActivity {
    TextInputLayout updatenum;
    Button save, uploadImage;
    ImageView imageView;

    private ProgressDialog progressDialog;
    private Uri imageUri;
    private final int GALLERY_REQ_CODE = 1000;
    List<Uri> imageUris = new ArrayList<>();
    LinearLayout imageContainer;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://apptest-f7922-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        updatenum = findViewById(R.id.phoneinput);
        save = findViewById(R.id.savenum);
        imageView = findViewById(R.id.imageview);
        uploadImage = findViewById(R.id.upload);
        imageContainer = findViewById(R.id.imageContainer);
        displayall();



        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            reference.child("User").child(currentUserId).child("imageURL").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String imageUrl = snapshot.getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            loadImage(imageUrl);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        if (currentUserId != null) {
            reference.child("User").child(currentUserId).child("imageURL").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String imageUrl = snapshot.getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(other.this).load(imageUrl).into(imageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPhoneNumber = updatenum.getEditText().getText().toString();
                reference.child("User").child(currentUserId).child("phone").setValue(newPhoneNumber);
                Toast.makeText(other.this, "Phone number updated", Toast.LENGTH_SHORT).show();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == GALLERY_REQ_CODE) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
                imageUris.add(imageUri);
                uploadImages(imageUris);

                for (Uri uri : imageUris) {
                    ImageView newImageView = new ImageView(this);
                    newImageView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    newImageView.setImageURI(uri);
                    imageContainer.addView(newImageView);
                }
            }
        }
    }

    private void uploadImages(List<Uri> imageUris) {
        String currentUserId = getCurrentUserId();

        if (currentUserId != null) {
            for (int i = 0; i < imageUris.size(); i++) {
                Uri imageUri = imageUris.get(i);
                String uniqueImageId = System.currentTimeMillis() + "_" + i;

                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                        .child("user_images")
                        .child(currentUserId)
                        .child("image_" + uniqueImageId + ".jpg");

                UploadTask uploadTask = storageRef.putFile(imageUri);

                uploadTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Image " + uniqueImageId + " Uploaded Successfully", Toast.LENGTH_SHORT).show();

                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();

                            reference.child("User").child(currentUserId).child("images").child("image_" + uniqueImageId).setValue(downloadUrl)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(this, "Image " + uniqueImageId + " URL updated in the database", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Failed to update Image " + uniqueImageId + " URL in the database", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    } else {
                        Exception e = task.getException();
                        Toast.makeText(this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
        displayall();

    }

    private String getCurrentUserId() {
        return MainActivity.currentuser;
    }


    private void loadImage(String imageUrl) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        Glide.with(this).load(imageUrl).into(imageView);

        imageContainer.addView(imageView);
    }


    private void displayall() {
        String currentUserId = getCurrentUserId();

        if (currentUserId != null) {
            reference.child("User").child(currentUserId).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot imageSnapshot : snapshot.getChildren()) {
                        String imageUrl = imageSnapshot.getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            loadImage(imageUrl);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}