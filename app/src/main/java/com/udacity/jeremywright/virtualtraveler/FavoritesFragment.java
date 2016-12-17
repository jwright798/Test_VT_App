package com.udacity.jeremywright.virtualtraveler;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.udacity.jeremywright.virtualtraveler.adapter.PhotoGridAdapter;
import com.udacity.jeremywright.virtualtraveler.contentprovider.PhotosContentProvider;
import com.udacity.jeremywright.virtualtraveler.dataobjects.PhotoDO;

import java.util.ArrayList;

/**
 * Created by itg5796 on 12/17/16.
 */

public class FavoritesFragment extends Fragment {

    private ArrayList<PhotoDO> favoritesList;
    PhotoGridAdapter adapter;

    public FavoritesFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View favoritesView =  inflater.inflate(R.layout.fragment_favorites, container, false);
        favoritesList = new ArrayList<PhotoDO>();

        adapter = new PhotoGridAdapter(getActivity(), favoritesList, true);

        GridView gridView = (GridView)favoritesView.findViewById(R.id.favorites_gridview);
        gridView.setAdapter(adapter);

        if (savedInstanceState == null) {
            //insert map pins from ContentProvider
            String URL = "content://com.udacity.jeremywright.virtualtraveler.contentprovider.PhotosContentProvider";
            Uri photos = Uri.parse(URL);
            Cursor c = getActivity().managedQuery(photos, null, null, null, null);
            ArrayList<PhotoDO> displayList = new ArrayList<PhotoDO>();
            if (c.moveToFirst()) {
                do{
                    PhotoDO photo = new PhotoDO(c.getString(c.getColumnIndex(PhotosContentProvider.PHOTOURL)));
                    displayList.add(photo);
                } while (c.moveToNext());
            }
            adapter.clear();
            adapter.addAll(displayList);
            favoritesList = displayList;
            adapter.notifyDataSetChanged();
        } else {
            adapter.clear();
            favoritesList = savedInstanceState.getParcelableArrayList("favoritesList");
            adapter.addAll(favoritesList);
            adapter.notifyDataSetChanged();
        }

        return favoritesView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("favoritesList", favoritesList);
    }
}
