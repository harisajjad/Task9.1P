package com.example.Task91P;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lost_and_found.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statement to create the advert table
    private static final String SQL_CREATE_ADVERT_TABLE =
            "CREATE TABLE " + DatabaseContract.AdvertEntry.TABLE_NAME + " (" +
                    DatabaseContract.AdvertEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DatabaseContract.AdvertEntry.COLUMN_POST_TYPE + " TEXT," +
                    DatabaseContract.AdvertEntry.COLUMN_NAME + " TEXT," +
                    DatabaseContract.AdvertEntry.COLUMN_PHONE + " TEXT," +
                    DatabaseContract.AdvertEntry.COLUMN_DESCRIPTION + " TEXT," +
                    DatabaseContract.AdvertEntry.COLUMN_DATE + " TEXT," +
                    DatabaseContract.AdvertEntry.COLUMN_LOCATION + " TEXT)";

    // SQL statement to delete the advert table
    private static final String SQL_DELETE_ADVERT_TABLE =
            "DROP TABLE IF EXISTS " + DatabaseContract.AdvertEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the advert table
        db.execSQL(SQL_CREATE_ADVERT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing advert table and recreate it
        db.execSQL(SQL_DELETE_ADVERT_TABLE);
        onCreate(db);
    }

    public void insertAdvert(Advert advert) {
        SQLiteDatabase db = getWritableDatabase();

        // Create a ContentValues object and put the advert values
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.AdvertEntry.COLUMN_POST_TYPE, advert.getLostOrFound());
        values.put(DatabaseContract.AdvertEntry.COLUMN_NAME, advert.getName());
        values.put(DatabaseContract.AdvertEntry.COLUMN_PHONE, advert.getPhone());
        values.put(DatabaseContract.AdvertEntry.COLUMN_DESCRIPTION, advert.getDescription());
        values.put(DatabaseContract.AdvertEntry.COLUMN_DATE, advert.getDate());
        values.put(DatabaseContract.AdvertEntry.COLUMN_LOCATION, advert.getLocation());

        // Insert the advert values into the database
        db.insert(DatabaseContract.AdvertEntry.TABLE_NAME, null, values);
        db.close();
    }

    public List<Advert> getAllAdverts() {
        List<Advert> advertList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        // Define the columns to retrieve
        String[] projection = {
                DatabaseContract.AdvertEntry._ID,
                DatabaseContract.AdvertEntry.COLUMN_POST_TYPE,
                DatabaseContract.AdvertEntry.COLUMN_NAME,
                DatabaseContract.AdvertEntry.COLUMN_PHONE,
                DatabaseContract.AdvertEntry.COLUMN_DESCRIPTION,
                DatabaseContract.AdvertEntry.COLUMN_DATE,
                DatabaseContract.AdvertEntry.COLUMN_LOCATION
        };

        // Query the database to retrieve all adverts
        Cursor cursor = db.query(
                DatabaseContract.AdvertEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            // Iterate through the cursor and create Advert objects
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.AdvertEntry._ID));
                String lostOrFound = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdvertEntry.COLUMN_POST_TYPE));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdvertEntry.COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdvertEntry.COLUMN_PHONE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdvertEntry.COLUMN_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdvertEntry.COLUMN_DATE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AdvertEntry.COLUMN_LOCATION));

                Advert advert = new Advert(id, lostOrFound, name, phone, description, date, location);
                advertList.add(advert);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return advertList;
    }

    public class DatabaseContract {

        private DatabaseContract() {
            // Private constructor to prevent instantiation
        }

        public class AdvertEntry implements BaseColumns {
            public static final String TABLE_NAME = "advert";
            public static final String _ID = "_id";
            public static final String COLUMN_POST_TYPE = "post_type";
            public static final String COLUMN_NAME = "name";
            public static final String COLUMN_PHONE = "phone";
            public static final String COLUMN_DESCRIPTION = "description";
            public static final String COLUMN_DATE = "date";
            public static final String COLUMN_LOCATION = "location";
        }
    }
}
