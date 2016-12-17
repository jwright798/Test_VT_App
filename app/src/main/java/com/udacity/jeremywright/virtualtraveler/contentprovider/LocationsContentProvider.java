package com.udacity.jeremywright.virtualtraveler.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;


//Used this as a reference and example https://www.tutorialspoint.com/android/android_content_providers.htm
public class LocationsContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.udacity.jeremywright.virtualtraveler.contentprovider.LocationsContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/locations";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String LAT = "latitude";
    public static final String LONGITUDE = "longitude";

    private static HashMap<String, String> LOCATIONS_PROJECTION_MAP;

    static final int LOCATIONS = 1;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "locations", LOCATIONS);
    }

    /**
     * Database specific constant declarations
     */

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "LocationsDB";
    static final String LOCATIONS_TABLE_NAME = "Locations";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + LOCATIONS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " latitude TEXT NOT NULL, " +
                    " longitude TEXT NOT NULL);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  LOCATIONS_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new location
         */
        long rowID = db.insert(	LOCATIONS_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            Log.i("LCP", "successfully inserted row into DB");
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(LOCATIONS_TABLE_NAME);

        if (uriMatcher.match(uri) == LOCATIONS){
            qb.setProjectionMap(LOCATIONS_PROJECTION_MAP);
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on id
             */
            sortOrder = _ID;
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        if (uriMatcher.match(uri) == LOCATIONS) {
            count = db.delete(LOCATIONS_TABLE_NAME, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int count = 0;

        if (uriMatcher.match(uri) == LOCATIONS) {
            count = db.update(LOCATIONS_TABLE_NAME, values, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case LOCATIONS:
                return "vnd.android.cursor.dir/vnd.example.locations";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
