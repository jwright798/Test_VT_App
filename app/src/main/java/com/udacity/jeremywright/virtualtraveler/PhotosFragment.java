package com.udacity.jeremywright.virtualtraveler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

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
public class PhotosFragment extends Fragment implements OnMapReadyCallback, PhotoServiceHelper.PhotoServiceDelegate {

    private static final String ARG_LAT = "latitude";
    private static final String ARG_LONG = "longitude";

    private MapView mapView;
    private GoogleMap map;

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
        serviceHelper.setPhotoServiceDelegate(this);

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
            latitude = getActivity().getIntent().getExtras().getDouble("latitude");
            longitude = getActivity().getIntent().getExtras().getDouble("longitude");
        }

        // Inflate the layout for this fragment
        final View photoView =  inflater.inflate(R.layout.fragment_photos, container, false);



        photoList = new ArrayList<PhotoDO>();

        adapter = new PhotoGridAdapter(getActivity(), photoList);

        GridView gridView = (GridView)photoView.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

        if (savedInstanceState == null) {
            serviceHelper.getPhotos(latitude, longitude);
        } else {
            adapter.clear();
            photoList = savedInstanceState.getParcelableArrayList("photoList");
            adapter.addAll(photoList);
            adapter.notifyDataSetChanged();
        }
        serviceHelper.getPhotos(latitude, longitude);

        return photoView;
    }


    //http://stackoverflow.com/questions/35496493/getmapasync-in-fragment
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mapView = (MapView) view.findViewById(R.id.scaled_map);
        if (savedInstanceState != null) {
            //removing this before the mapView onCreate gets called
            savedInstanceState.remove("photoList");
        }
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng current = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(current));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current,18.0f));
        map.setContentDescription("Map for selected pin");
    }

    @Override
    public void photosResponse(ArrayList<PhotoDO> photos) {
        if (photos != null){
            adapter.clear();
            adapter.addAll(photos);
            photoList=photos;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("longitude", longitude);
        outState.putDouble("latitude", latitude);
        outState.putParcelableArrayList("photoList", photoList);
    }
}
