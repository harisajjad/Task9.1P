package com.example.Task91P;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllAdvert extends AppCompatActivity implements AdvertAdapter.OnItemClickListener {

    private TextView textViewHeading;
    private TextView textViewNoData;
    private RecyclerView recyclerViewLostAndFound;
    private AdvertAdapter advertAdapter;

    private static final int REMOVE_ADVERT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advert_all);

        textViewHeading = findViewById(R.id.textViewHeading);
        textViewNoData = findViewById(R.id.textViewNoData);
        recyclerViewLostAndFound = findViewById(R.id.recyclerViewLostAndFound);

        // Set up RecyclerView
        recyclerViewLostAndFound.setLayoutManager(new LinearLayoutManager(this));
        advertAdapter = new AdvertAdapter(new ArrayList<>(), this); // Pass the listener
        recyclerViewLostAndFound.setAdapter(advertAdapter);

        // Fetch data from the database
        updateAdvertList();
    }

    // Fetches all adverts from the database and updates the RecyclerView
    public void updateAdvertList() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Advert> advertList = databaseHelper.getAllAdverts();
        advertAdapter.setAdvertList(advertList);
        advertAdapter.notifyDataSetChanged();
        checkDataEmpty(advertList);
    }

    // Checks if the advert list is empty and updates the UI accordingly
    private void checkDataEmpty(List<Advert> advertList) {
        if (advertList.isEmpty()) {
            showNoDataMessage();
        } else {
            showAdverts();
        }
    }

    // Shows the "No Data" message when the advert list is empty
    private void showNoDataMessage() {
        textViewNoData.setVisibility(View.VISIBLE);
        recyclerViewLostAndFound.setVisibility(View.GONE);
    }

    // Shows the RecyclerView when there are adverts to display
    private void showAdverts() {
        textViewNoData.setVisibility(View.GONE);
        recyclerViewLostAndFound.setVisibility(View.VISIBLE);
    }

    // Handles the item click event from the RecyclerView
    @Override
    public void onItemClickListener(long advertId) {
        // Handle item click event
        Intent intent = new Intent(AllAdvert.this, AdvertRemover.class);
        intent.putExtra("advert_id", advertId); // Pass the advert ID to the next activity
        startActivityForResult(intent, REMOVE_ADVERT_REQUEST_CODE);
    }

    // Handles the result from the RemoveAdvertActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REMOVE_ADVERT_REQUEST_CODE && resultCode == RESULT_OK) {
            updateAdvertList();
        }
    }
}
