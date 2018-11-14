package ru.geekbrains.retrofit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database City
    private static final String DATABASE_NAME = "android_api";

    // Login table city
    private static final String TABLE_WEATHER = "weather";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CITY = "city";
    private static final String KEY_TEMP = "temperature";
    private static final String KEY_PRESSURE = "pressure";
    private static final String KEY_HUMIDITY = "humidity";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_WEATHER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CITY + " TEXT,"
                + KEY_TEMP + " TEXT," + KEY_PRESSURE + " TEXT,"
                + KEY_HUMIDITY + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing weather details in database
     * */

    public void addWeather(String city, String temperature, String pressure, String humidity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CITY, city); // City
        values.put(KEY_TEMP, temperature); // Temp
        values.put(KEY_PRESSURE, pressure); // Temp
        values.put(KEY_HUMIDITY, humidity); // Created At

        // Inserting Row
        long id = db.insert(TABLE_WEATHER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New weather inserted into sqlite: " + id);
    }

    /**
     * Getting weather data from database
     * */
    public HashMap<String, String> getWeatherDetails() {
        HashMap<String, String> weather = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_WEATHER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            weather.put("city", cursor.getString(1));
            weather.put("temperature", cursor.getString(2));
            weather.put("pressure", cursor.getString(3));
            weather.put("humidity", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return weather
        Log.d(TAG, "Fetching weather from Sqlite: " + weather.toString());

        return weather;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteWeather() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_WEATHER, null, null);
        db.close();

        Log.d(TAG, "Deleted all weather info from sqlite");
    }

}
