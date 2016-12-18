package com.udacity.jeremywright.virtualtraveler;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class FavoritesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayList<PhotoDO> favoritesList;
    PhotoGridAdapter adapter;
    private static final int LOADER_ID =2;

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
            //insert map pins from Loader
            getActivity().getSupportLoaderManager().initLoader(LOADER_ID,null,this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String URL = getString(R.string.photo_provider_url);
        return new CursorLoader(getActivity(),
                Uri.parse(URL)
                , null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<PhotoDO> displayList = new ArrayList<PhotoDO>();
        if (data.moveToFirst()) {
            do{
                PhotoDO photo = new PhotoDO(data.getString(data.getColumnIndex(PhotosContentProvider.PHOTOURL)));
                displayList.add(photo);
            } while (data.moveToNext());
        }
        adapter.clear();
        adapter.addAll(displayList);
        favoritesList = displayList;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoritesList = new ArrayList<PhotoDO>();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}
