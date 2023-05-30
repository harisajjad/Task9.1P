package com.example.Task91P;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdvertRemover extends AppCompatActivity {

    private TextView textViewHeading;
    private TextView textViewDetail;
    private Button buttonRemove;

    private DatabaseHelper databaseHelper;
    private long advertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remover_advert);

        textViewHeading = findViewById(R.id.textViewHeading);
        textViewDetail = findViewById(R.id.textViewDetail);
        buttonRemove = findViewById(R.id.buttonRemove);

        // Create an instance of DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Get the advert ID from the intent
        advertId = getAdvertIdFromIntent();

        // Set the text views with the advert details
        setAdvertDetails();

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the advert from the database
                if (removeAdvertFromDatabase(advertId)) {
                    // Display a success message
                    Toast.makeText(AdvertRemover.this, "Advert removed successfully", Toast.LENGTH_SHORT).show();

                    // Finish the activity or perform any other necessary actions
                    finish();
                } else {
                    // Display an error message
                    Toast.makeText(AdvertRemover.this, "Failed to remove advert", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Get the advert ID from the intent
    private long getAdvertIdFromIntent() {
        long advertId = -1; // Default value if the advert ID is not found in the intent extras

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            advertId = extras.getLong("advert_id", -1);
        }

        return advertId;
    }

    // Set the text views with the advert details
    private void setAdvertDetails() {
        long advertId = getAdvertIdFromIntent();

        // Retrieve the advert details from the database using the advertId
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String[] projection = {
                DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_NAME,
                DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_DESCRIPTION,
                DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_PHONE,
                DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_DATE,
                DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_LOCATION
                // Add other columns you want to retrieve
        };

        String selection = DatabaseHelper.DatabaseContract.AdvertEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(advertId)};

        Cursor cursor = database.query(
                DatabaseHelper.DatabaseContract.AdvertEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_DESCRIPTION));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_PHONE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_DATE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DatabaseContract.AdvertEntry.COLUMN_LOCATION));


            // Set the advert details in the views
            textViewHeading.setText(name);
            String details = "Description: " + description + "\nPhone: " + phone + "\nDate: " + date + "\nLocation: " + location;
            textViewDetail.setText(details);
            // Set other views with the retrieved data

            cursor.close();
        }

        database.close();
        databaseHelper.close();
    }

    // Remove the advert from the database
    private boolean removeAdvertFromDatabase(long advertId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        String selection = DatabaseHelper.DatabaseContract.AdvertEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(advertId)};

        int deletedRows = database.delete(
                DatabaseHelper.DatabaseContract.AdvertEntry.TABLE_NAME,
                selection,
                selectionArgs
        );

        database.close();
        databaseHelper.close();

        return deletedRows > 0;
    }

}
