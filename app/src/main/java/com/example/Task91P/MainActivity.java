package com.example.Task91P;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button createNewAdvertButton;
    private Button showAllItemsButton;
    private Button ShowOnMapsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNewAdvertButton = findViewById(R.id.createnew);
        showAllItemsButton = findViewById(R.id.Allshow);
        ShowOnMapsButton = findViewById(R.id.showmaps);

        createNewAdvertButton.setOnClickListener(this);
        showAllItemsButton.setOnClickListener(this);
        ShowOnMapsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createnew) {
            // Handle "Create a new advert" button click
            Intent createNewIntent = new Intent(MainActivity.this, AdvertActivity.class);
            startActivity(createNewIntent);
        } else if (v.getId() == R.id.Allshow) {
            // Handle "Show all lost and found" button click
            Intent showAllIntent = new Intent(MainActivity.this, AllAdvert.class);
            startActivity(showAllIntent);
        } else if (v.getId() == R.id.showmaps) {
            // Handle "Show On Maps" button click
            Intent showMapsIntent = new Intent(MainActivity.this, ShowOnMapsActivity.class);
            startActivity(showMapsIntent);
        }
    }
}