package com.udacity.jeremywright.virtualtraveler;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.udacity.jeremywright.virtualtraveler.contentprovider.LocationsContentProvider;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        // Add a marker in Sydney and move the camera
        LatLng sanAntonio = new LatLng(29.4241, -98.4931);
        mMap.addMarker(new MarkerOptions().position(sanAntonio).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sanAntonio,16.0f));

        //insert map pins from ContentProvider
        String URL = "content://com.udacity.jeremywright.virtualtraveler.contentprovider.LocationsContentProvider";
        Uri locations = Uri.parse(URL);
        Cursor c = managedQuery(locations, null, null, null, null);

        if (c.moveToFirst()) {
            do{
                double testLat = Double.parseDouble(c.getString(c.getColumnIndex(LocationsContentProvider.LAT)));
                double testLong = Double.parseDouble(c.getString(c.getColumnIndex(LocationsContentProvider.LONGITUDE)));
                String tag = c.getString(c.getColumnIndex(LocationsContentProvider._ID));
                LatLng marker = new LatLng(Double.parseDouble(c.getString(c.getColumnIndex(LocationsContentProvider.LAT))),
                        Double.parseDouble(c.getString(c.getColumnIndex(LocationsContentProvider.LONGITUDE))));
                mMap.addMarker(new MarkerOptions().position(marker).draggable(true).title(tag));
            } while (c.moveToNext());
        }

        //Disable Map Toolbar:
        mMap.getUiSettings().setMapToolbarEnabled(false);

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
    }


}
