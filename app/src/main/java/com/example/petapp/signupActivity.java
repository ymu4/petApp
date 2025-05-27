package com.example.petapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.io.ByteArrayOutputStream;

import Model.User;

public class signupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    EditText signupUsername,signupEmail, signupPassword, signupConfirm, signupphoneNumber;
    TextView signupButton, loginRedirectText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        
        setContentView(R.layout.signup);
        signupUsername = findViewById(R.id.usernameSignup);
        signupEmail = findViewById(R.id.EmailSignup);
        signupPassword = findViewById(R.id.passwordSignup);
        signupConfirm = findViewById(R.id.confirmPasswordSignup);
        signupButton = findViewById(R.id.signupButton);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupphoneNumber = findViewById(R.id.phone_number);



        signupEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Email validation
                    String email = signupEmail.getText().toString().trim();
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        signupEmail.setError("Enter a valid email address");
                    }
                }
            }
        });

        // Set onFocusChangeListener for password EditText
        signupPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Password validation
                    String password = signupPassword.getText().toString();
                    if (password.length() < 8) {
                        signupPassword.setError("Password must be at least 8 characters long");
                    }
                }
            }
        });

        // Sign up button click listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = signupUsername.getText().toString();
                final String email = signupEmail.getText().toString();
                final String password = signupPassword.getText().toString();
                final String confirmPassword = signupConfirm.getText().toString();
                final String phoneNumber = signupphoneNumber.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    signupUsername.setError("Please enter username");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    signupPassword.setError("Please enter password");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    signupConfirm.setError("Passwords do not match");
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    signupphoneNumber.setError("Please enter phone number");
                    return;
                }

             else {
                    checkEmailExists(email, new OnEmailCheckedListener() {
                        @Override
                        public void onEmailChecked(boolean exists) {
                            if (exists) {
                                Toast.makeText(signupActivity.this, "Email already exists. Please choose another email or log in.", Toast.LENGTH_SHORT).show();
                            } else {
                                checkUsernameExists(username, new OnUsernameCheckedListener() {
                                    @Override
                                    public void onUsernameChecked(boolean exists) {
                                        if (exists) {
                                            Toast.makeText(signupActivity.this, "Username already exists. Please choose another username.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            createUserWithEmailAndPassword(username, email, password, confirmPassword,phoneNumber);
                                            Intent intent = new Intent(signupActivity.this,MainActivity.class);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signupActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
        
        
    }
    private void createUserWithEmailAndPassword(String username, String email, String password, String confirmPassword, String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = user.getUid();
                            // Create a User object with the user information
                            User newUser = new User(userId, username, email, password,confirmPassword,phoneNumber, "");


                            // Store user data in Realtime Database
                            DatabaseReference userRef = mDatabase.child("users").child(userId);
                            userRef.setValue(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Upload default profile image to Firebase Storage
                                            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
                                            UploadTask uploadTask = storageRef.putFile(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.profile));
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    // Get the download URL of the uploaded image
                                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String imageUrl = uri.toString();
                                                            // Update the user's profile data with the profile image URL
                                                            userRef.child("profile_image").setValue(imageUrl)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            startActivity(new Intent(signupActivity.this, MainActivity.class));
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(signupActivity.this, "Failed to update profile image URL", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(signupActivity.this, "Failed to upload default profile image", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(signupActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(signupActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void checkEmailExists(String email, final OnEmailCheckedListener listener) {
        DatabaseReference usersRef = mDatabase.child("users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean emailExists = dataSnapshot.exists();
                listener.onEmailChecked(emailExists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onEmailChecked(false);
            }
        });
    }

    interface OnEmailCheckedListener {
        void onEmailChecked(boolean exists);
    }

    private void checkUsernameExists(String username, final OnUsernameCheckedListener listener) {
        DatabaseReference usersRef = mDatabase.child("users");
        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean usernameExists = dataSnapshot.exists();
                listener.onUsernameChecked(usernameExists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onUsernameChecked(false);
            }
        });
    }

    interface OnUsernameCheckedListener {
        void onUsernameChecked(boolean exists);
    }
}