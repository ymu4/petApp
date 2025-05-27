package com.example.petapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng selectedLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                selectedLatLng = latLng;
                Toast.makeText(SelectLocationActivity.this, "Lat:" + latLng.latitude + " lng:" + latLng.longitude, Toast.LENGTH_SHORT).show();

                // Set the result intent and finish the activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", selectedLatLng.latitude);
                resultIntent.putExtra("longitude", selectedLatLng.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // Add a marker in UAE and move the camera
        LatLng uae = new LatLng(getLatitude(), getLongitude());
        mMap.addMarker(new MarkerOptions().position(uae).title("Marker in UAE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uae));
    }

    private double getLatitude() {
        EditText et = (EditText) findViewById(R.id.lat);
        String latitudeString = et.getText().toString().trim();
        if (!latitudeString.isEmpty()) {
            return Double.parseDouble(latitudeString);
        } else {
            // Return a default latitude value if the EditText is empty
            return 0.0;
        }
    }

    private double getLongitude() {
        EditText et = (EditText) findViewById(R.id.lng);
        String longitudeString = et.getText().toString().trim();
        if (!longitudeString.isEmpty()) {
            return Double.parseDouble(longitudeString);
        } else {
            // Return a default longitude value if the EditText is empty
            return 0.0;
        }
    }

    public void updateMap(View view) {
        // Add a marker in UAE and move the camera
        LatLng uae = new LatLng(getLatitude(), getLongitude());
        mMap.addMarker(new MarkerOptions().position(uae).title("Marker in UAE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uae));
    }
}