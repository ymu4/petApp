package com.example.petapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import Model.Post;

public class NewPostActivity extends AppCompatActivity {

    private EditText etCaption, etCity, etStreet;
    private Button shareButton;
    ImageView backButton;
    private TextView tvloca;
    private EditText etspecies;
    private EditText etage;
    private CheckBox checkBox;
    private RadioButton rbfemale;
    private RadioButton rbmale;
    private ProgressBar progressBar;
    private ImageView imageView;


    private Uri selectedImageUri;
    double latitude;
    double longitude;
    private long postTimestamp;
    private static final int REQUEST_SELECT_LOCATION = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post);


        etCaption = findViewById(R.id.captionEditText);
        shareButton = findViewById(R.id.shareButton);
        progressBar = findViewById(R.id.progressBar);
        //tvloca = findViewById(R.id.tvLocation);
        etspecies = findViewById(R.id.petSpeciesEditText);

        checkBox = findViewById(R.id.injuredCheckBox);
        rbfemale = findViewById(R.id.femaleRadioButton);
        rbmale = findViewById(R.id.maleRadioButton);
        imageView = findViewById(R.id.imageView);
        etCity = findViewById(R.id.etCity);
        etStreet = findViewById(R.id.etStreet);
        backButton = findViewById(R.id.back_nav);


        String caption = etCaption.getText().toString();
        String species = etspecies.getText().toString();
        String city = etCity.getText().toString();
        String street = etStreet.getText().toString();
        boolean injured = checkBox.isChecked();
        String gender = rbfemale.isChecked() ? "female" : (rbmale.isChecked() ? "male" : "unknown");
        postTimestamp = System.currentTimeMillis();

        // get location
//        tvloca.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(NewPostActivity.this, SelectLocationActivity.class);
//                startActivityForResult(intent, REQUEST_SELECT_LOCATION);
//            }
//        });

        //back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //get image uri
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to select image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = etCaption.getText().toString();
                String species = etspecies.getText().toString();
                boolean injured;
                String gender = rbfemale.isChecked() ? "female" : (rbmale.isChecked() ? "male" : "unknown");
                postTimestamp = System.currentTimeMillis();

                if (checkBox.isChecked()) {
                    injured = true;
                } else {
                    injured = false;
                }

                if (species.isEmpty()) {
                    etspecies.setError("Species is required");
                    etspecies.requestFocus();
                    return;
                }

                if (selectedImageUri == null) {
                    Toast.makeText(NewPostActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the current user's ID
                String userId = getCurrentUserId();

                // Get a reference to the "posts" node in your Firebase Realtime Database
                DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

                // Generate a new unique key for the post
                String postId = postsRef.push().getKey();

                // Upload the selected image to Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("post_images").child(postId);
                UploadTask uploadTask = storageRef.putFile(selectedImageUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded image
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                // Create a new Post object with the image URL
                                Post post = new Post(userId, species, gender, caption, postTimestamp, city, street, imageUrl, injured);
                                // Save the post to the database
                                postsRef.child(postId).setValue(post)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Post added successfully
                                                Toast.makeText(NewPostActivity.this, "Post shared successfully", Toast.LENGTH_SHORT).show();
                                                // Redirect to the main activity or any other desired activity
                                                Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle image upload failure
                        Toast.makeText(NewPostActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                // Handle selected image URI
                if (data != null) {
                    selectedImageUri = data.getData();
                    // Convert URI to Bitmap
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        // Resize bitmap
                        Bitmap resizedBitmap = scaleDownBitmap(bitmap, 400); // Set your desired max size here
                        imageView.setImageBitmap(resizedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (requestCode == REQUEST_SELECT_LOCATION) {
            if (data != null) {
                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);
            }
        }
    }


    private Bitmap scaleDownBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }
}



