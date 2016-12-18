package com.udacity.jeremywright.virtualtraveler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.udacity.jeremywright.virtualtraveler.adapter.PhotoGridAdapter;
import com.udacity.jeremywright.virtualtraveler.dataobjects.PhotoDO;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotosFragment extends Fragment implements OnMapReadyCallback, LoaderManager.LoaderCallbacks {

    private static final String ARG_LAT = "latitude";
    private static final String ARG_LONG = "longitude";
    private static final String ARG_PHOTOLIST = "photoList";
    private static final int PHOTO_LOADER = 1;

    private MapView mapView;
    private GoogleMap map;
    private FloatingActionButton fab;

    private double latitude;
    private double longitude;

    private PhotoServiceHelper serviceHelper = new PhotoServiceHelper();
    private ArrayList<PhotoDO> photoList;


    PhotoGridAdapter adapter;

    public PhotosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param latitude Parameter 1.
     * @param longitude Parameter 2.
     * @return A new instance of fragment PhotosFragment.
     */
    public static PhotosFragment newInstance(double param1, double param2) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, param1);
        args.putDouble(ARG_LONG, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // serviceHelper.setPhotoServiceDelegate(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            latitude = getArguments().getDouble(ARG_LAT);
            longitude = getArguments().getDouble(ARG_LONG);
        }
        else{
            latitude = getActivity().getIntent().getExtras().getDouble(ARG_LAT);
            longitude = getActivity().getIntent().getExtras().getDouble(ARG_LONG);
        }

        // Inflate the layout for this fragment
        final View photoView =  inflater.inflate(R.layout.fragment_photos, container, false);


        photoList = new ArrayList<PhotoDO>();

        adapter = new PhotoGridAdapter(getActivity(), photoList, false);

        GridView gridView = (GridView)photoView.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

        if (savedInstanceState == null) {
            Bundle loaderArgs = new Bundle();
            loaderArgs.putString(ARG_LAT, Double.toString(latitude));
            loaderArgs.putString(ARG_LONG, Double.toString(longitude));
            getActivity().getSupportLoaderManager().initLoader(PHOTO_LOADER, loaderArgs, this).forceLoad();
        } else {
            adapter.clear();
            photoList = savedInstanceState.getParcelableArrayList(ARG_PHOTOLIST);
            adapter.addAll(photoList);
            adapter.notifyDataSetChanged();
        }

        return photoView;
    }


    //http://stackoverflow.com/questions/35496493/getmapasync-in-fragment
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mapView = (MapView) view.findViewById(R.id.scaled_map);
        if (savedInstanceState != null) {
            //removing this before the mapView onCreate gets called
            savedInstanceState.remove(ARG_PHOTOLIST);
        }
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setAllGesturesEnabled(false);
        LatLng current = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(current));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current,18.0f));
        map.setContentDescription(getString(R.string.photo_pin_content_desc));
    }

    @Override
    public Loader<ArrayList<PhotoDO>> onCreateLoader(int id, Bundle args) {
        String testLat = args.getString(ARG_LAT);
        String testLong = args.getString(ARG_LONG);
        return new PhotosAsyncTaskLoader(getActivity(), args.getString(ARG_LAT), args.getString(ARG_LONG));
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        photoList = (ArrayList<PhotoDO>) data;
        adapter.clear();
        adapter.addAll((ArrayList<PhotoDO>) data);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onLoaderReset(Loader loader) {
        photoList = new ArrayList<PhotoDO>();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(ARG_LONG, longitude);
        outState.putDouble(ARG_LAT, latitude);
        outState.putParcelableArrayList(ARG_PHOTOLIST, photoList);
    }
}
