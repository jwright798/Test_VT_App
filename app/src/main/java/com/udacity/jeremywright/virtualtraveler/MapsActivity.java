package com.udacity.jeremywright.virtualtraveler;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.udacity.jeremywright.virtualtraveler.contentprovider.LocationsContentProvider;

import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor>,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private FloatingActionButton fab;
    private Location mLastLocation;
    private SupportMapFragment mapFragment;
    private static final int LOADER_ID =3;
    private static final int LOCATION_PERM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fab = (FloatingActionButton) findViewById(R.id.map_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });


        // Create an instance of GoogleAPIClient.
        //https://developer.android.com/training/location/retrieve-current.html
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LatLng lastLoc = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(lastLoc).draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc,16.0f));
        } else {
            //no location for some reason, so set a test pin, just to get the flow moving
            LatLng sanAntonio = new LatLng(29.4241, -98.4931);
            mLastLocation = new Location("");
            mLastLocation.setLatitude(sanAntonio.latitude);
            mLastLocation.setLongitude(sanAntonio.longitude);
            mMap.addMarker(new MarkerOptions().position(sanAntonio).draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sanAntonio,16.0f));
        }

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //how to get indivitual marker info
                marker.hideInfoWindow();
                Log.i("VT","Latitude"+Double.toString(marker.getPosition().latitude));
                Log.i("VT","Longitude"+Double.toString(marker.getPosition().longitude));
                Intent intent = new Intent(MapsActivity.this, PhotosActivity.class);
                intent.putExtra("latitude",marker.getPosition().latitude);
                intent.putExtra("longitude",marker.getPosition().longitude);
                startActivity(intent);
                return false;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Add a new marker
                mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                Toast.makeText(MapsActivity.this, "Pin added", Toast.LENGTH_SHORT);
                createNewRecord(latLng.latitude, latLng.longitude);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(final Marker marker) {
                final Marker thisMarker = marker;
                AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this).create();
                dialog.setTitle("Remove");
                dialog.setMessage("Remove this pin?");
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE,"NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setButton(AlertDialog.BUTTON_POSITIVE,"YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRecord(marker.getTitle());
                        thisMarker.remove();
                    }
                });
                dialog.show();
            }

            @Override
            public void onMarkerDrag(final Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });

    }

    private void createNewRecord(double lat, double longitude){
        // Add a new student record
        ContentValues values = new ContentValues();
        values.put(LocationsContentProvider.LAT, Double.toString(lat));

        values.put(LocationsContentProvider.LONGITUDE,
                Double.toString(longitude));

        Uri uri = getContentResolver().insert(
                LocationsContentProvider.CONTENT_URI, values);

    }

    private void deleteRecord(String id){

        int result = getContentResolver().delete(LocationsContentProvider.CONTENT_URI, "_id=?", new String[]{id});
        if (result == 0){
            Log.e("VT", "error deleting location");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //insert map pins from ContentProvider
        String URL = "content://com.udacity.jeremywright.virtualtraveler.contentprovider.LocationsContentProvider";
        return new CursorLoader(this,
                Uri.parse(URL)
                , null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            do{
                double testLat = Double.parseDouble(data.getString(data.getColumnIndex(LocationsContentProvider.LAT)));
                double testLong = Double.parseDouble(data.getString(data.getColumnIndex(LocationsContentProvider.LONGITUDE)));
                String tag = data.getString(data.getColumnIndex(LocationsContentProvider._ID));
                LatLng marker = new LatLng(Double.parseDouble(data.getString(data.getColumnIndex(LocationsContentProvider.LAT))),
                        Double.parseDouble(data.getString(data.getColumnIndex(LocationsContentProvider.LONGITUDE))));
                mMap.addMarker(new MarkerOptions().position(marker).draggable(true).title(tag));
            } while (data.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMap.clear();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

           // mMap.setMyLocationEnabled(true);
            mapFragment.getMapAsync(this);
        } else {
            checkForLocationPermission();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mapFragment.getMapAsync(this);
    }

    //https://developer.android.com/training/permissions/requesting.html
    private void checkForLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this).setMessage("Location needed to show your current location on map").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERM );
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //We don't have permission, so disable location
                        LatLng sanAntonio = new LatLng(29.4241, -98.4931);
                        mLastLocation = new Location("");
                        mLastLocation.setLatitude(sanAntonio.latitude);
                        mLastLocation.setLongitude(sanAntonio.longitude);
                        mapFragment.getMapAsync(MapsActivity.this);
                        dialog.dismiss();
                    }
                }).show();
            } else{
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERM );
            }
        } else {
            // We have the permission
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    //https://developer.android.com/training/permissions/requesting.html
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_PERM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //don't need to add check or handle exception since we have permission
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    mapFragment.getMapAsync(this);

                } else {
                    //we don't have permission, so disable the location functionality
                    LatLng sanAntonio = new LatLng(29.4241, -98.4931);
                    mLastLocation = new Location("");
                    mLastLocation.setLatitude(sanAntonio.latitude);
                    mLastLocation.setLongitude(sanAntonio.longitude);
                    mapFragment.getMapAsync(this);
                }
                return;
            }
        }
    }
}
