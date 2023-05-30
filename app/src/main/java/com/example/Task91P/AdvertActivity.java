package com.example.Task91P;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AdvertActivity extends AppCompatActivity {

    private RadioGroup radioGroupContainer;
    private RadioButton radioButtonLost;
    private RadioButton radioButtonFound;
    private EditText etName;
    private EditText etPhone;
    private EditText etDescription;
    private EditText etDate;
    private EditText etLocation;
    private Button btnSave;
    private Button btnCurrentLocation;

    private DatabaseHelper dbHelper;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_PICKER_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        radioGroupContainer = findViewById(R.id.radioGroupLostAndFound);
        radioButtonLost = findViewById(R.id.rbLost);
        radioButtonFound = findViewById(R.id.rbFound);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        btnSave = findViewById(R.id.btnSave);
        btnCurrentLocation = findViewById(R.id.btncurrent_location);

        // Set click listener for the save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAdvert();
            }
        });

        // Set click listener for the location EditText
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an activity to select the location using Google Maps
                // Handle the result in onActivityResult() method
                Intent intent = new Intent(AdvertActivity.this, LocationPickerActivity.class);
                startActivityForResult(intent, LOCATION_PICKER_REQUEST_CODE);
            }
        });

        // Set click listener for the current location button
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    private void saveAdvert() {
        // Get the values entered by the user
        String postType = radioButtonLost.isChecked() ? "Lost" : "Found";
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Get a writable database instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new ContentValues object and put the values
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_POST_TYPE, postType);
        values.put(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_NAME, name);
        values.put(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_DATE, date);
        values.put(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_LOCATION, location);

        // Insert the values into the database table
        long newRowId = db.insert(DatabaseHelper.DatabaseContract.AdvertEntry.TABLE_NAME, null, values);

        // Check if the insertion was successful
        if (newRowId != -1) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if it is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Start listening for location updates
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        // Initialize location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Remove location updates to save battery
                locationManager.removeUpdates(this);

                // Get the current latitude and longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Set the current location in the EditText
                String currentLocation = latitude + "," + longitude;
                etLocation.setText(currentLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Request location updates
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the selected location from the result Intent
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);
            String selectedLocation = latitude + "," + longitude;

            // Set the selected location in the EditText
            etLocation.setText(selectedLocation);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start listening for location updates
                startLocationUpdates();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clean up location listener
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
