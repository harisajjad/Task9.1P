package com.example.Task91P;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class ShowOnMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_on_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Retrieve the location data from the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Advert> advertList = databaseHelper.getAllAdverts();

        // Iterate through the list of adverts
        for (Advert advert : advertList) {
            String location = advert.getLocation();

            // Split the location string into latitude and longitude
            String[] latLng = location.split(",");
            if (latLng.length == 2) {
                double latitude = Double.parseDouble(latLng[0]);
                double longitude = Double.parseDouble(latLng[1]);

                // Create a LatLng object with the latitude and longitude
                LatLng advertLocation = new LatLng(latitude, longitude);

                // Add a marker on the map at the advert location
                googleMap.addMarker(new MarkerOptions().position(advertLocation));
            }
        }

        // Customize the map as needed
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Move the camera to the last advert location
        if (!advertList.isEmpty()) {
            Advert lastAdvert = advertList.get(advertList.size() - 1);
            String lastLocation = lastAdvert.getLocation();
            String[] lastLatLng = lastLocation.split(",");

        }
    }
}
