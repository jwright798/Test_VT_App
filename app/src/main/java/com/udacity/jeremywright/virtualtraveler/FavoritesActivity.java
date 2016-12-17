package com.udacity.jeremywright.virtualtraveler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by itg5796 on 12/17/16.
 */

public class FavoritesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        getSupportActionBar().setTitle("Favorites");

        if (savedInstanceState == null){
            FavoritesFragment fragment = new FavoritesFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.favorites_container, fragment).commit();
        }
    }

}
