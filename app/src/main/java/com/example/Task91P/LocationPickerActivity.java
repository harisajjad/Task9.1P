package com.example.Task91P;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class LocationPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int DEFAULT_ZOOM = 15;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 123; // Use your desired request code

    private MapView mapView;
    private GoogleMap googleMap;
    private Button btnSelectLocation;
    private EditText etSearch;

    private LatLng selectedLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker selectedMarker;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        // Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyDjiHt-Vhd2JHoBobFFu6FXC2MFSdZNZGQ");

        // Initialize views
        mapView = findViewById(R.id.mapView);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);
        etSearch = findViewById(R.id.etSearch);

        // Initialize the map view
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize the fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a location request with high accuracy
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create a location callback to handle location updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                // Get the last known location from the result
                Location lastLocation = locationResult.getLastLocation();

                // Update the map camera to the current location
                LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));

                // Add a marker for the current location
                if (selectedMarker != null) {
                    selectedMarker.remove();
                }
                selectedMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));

                // Update the selected location
                selectedLocation = currentLocation;
            }
        };

        // Set click listener for the select location button
        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocation != null) {
                    // Return the selected location back to the calling activity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("latitude", selectedLocation.latitude);
                    resultIntent.putExtra("longitude", selectedLocation.longitude);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(LocationPickerActivity.this, "No location selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for the search button
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the bounds for autocomplete search
                LatLng southwestBounds = new LatLng(37.7749, -122.4194); // Example: San Francisco
                LatLng northeastBounds = new LatLng(37.8095, -122.3927);

                // Start the autocomplete activity with the defined bounds
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setLocationRestriction(RectangularBounds.newInstance(southwestBounds, northeastBounds))
                        .build(LocationPickerActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Check if Google Play services is available
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            // Google Play services is not available
            Toast.makeText(this, "Google Play services is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enable zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, show current location
            startLocationUpdates();
        }

        // Set click listener for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Clear previous marker (if any)
                if (selectedMarker != null) {
                    selectedMarker.remove();
                }

                // Add a new marker at the clicked location
                selectedMarker = googleMap.addMarker(new MarkerOptions().position(latLng));

                // Update the selected location
                selectedLocation = latLng;
            }
        });
    }

    private void startLocationUpdates() {
        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    private void stopLocationUpdates() {
        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Retrieve the selected place's information
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place != null) {
                    // Clear previous marker (if any)
                    if (selectedMarker != null) {
                        selectedMarker.remove();
                    }

                    // Add a new marker at the selected location
                    LatLng selectedLatLng = place.getLatLng();
                    selectedMarker = googleMap.addMarker(new MarkerOptions().position(selectedLatLng));

                    // Update the selected location
                    selectedLocation = selectedLatLng;

                    // Move the map camera to the selected location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, DEFAULT_ZOOM));
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Show an error message
                Status status = Autocomplete.getStatusFromIntent(data);
                if (status != null) {
                    Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, start location updates
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
