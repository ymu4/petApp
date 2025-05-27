package com.example.petapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No need for a layout file for this activity

        // Check if the user is already signed in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, navigate to the main page
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // User is not signed in, navigate to the login page
            startActivity(new Intent(SplashActivity.this, loginActivity.class));
        }

        // Finish the splash activity to prevent the user from going back to it
        finish();
    }
}

