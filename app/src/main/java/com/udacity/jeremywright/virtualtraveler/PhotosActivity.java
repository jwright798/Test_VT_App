package com.udacity.jeremywright.virtualtraveler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PhotosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        getSupportActionBar().setTitle(R.string.photos_title);

        if (savedInstanceState == null){
            PhotosFragment fragment = new PhotosFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.photo_container, fragment).commit();
        }
    }
}
