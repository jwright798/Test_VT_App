package com.udacity.jeremywright.virtualtraveler.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.jeremywright.virtualtraveler.R;
import com.udacity.jeremywright.virtualtraveler.contentprovider.PhotosContentProvider;
import com.udacity.jeremywright.virtualtraveler.dataobjects.PhotoDO;

import java.util.List;

/**
 * Created by itg5796 on 12/13/16.
 */

public class PhotoGridAdapter extends ArrayAdapter<PhotoDO> {

    private boolean isFavorites;
    public PhotoGridAdapter(Context context, List<PhotoDO> photoDOs, boolean isFavorites) {
        super(context, 0, photoDOs);
        this.isFavorites = isFavorites;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final PhotoDO photo = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_grid_item, parent, false);
        }

        ImageView photoImageView = (ImageView)convertView.findViewById(R.id.grid_image_view);
        Log.v("photo", photo.getURL());
        Picasso.with(getContext()).load(photo.getURL()).into(photoImageView);

        photoImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, photo.getURL());
                sendIntent.setType("text/plain");
                getContext().startActivity(sendIntent);
                return false;
            }
        });

        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Note: I was originally going to make this a double tap action, but that's against UI guidelines
                if (!isFavorites){
                    createNewRecord(photo.getURL());
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    remove(photo);
                    removePhoto(photo);
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }
    private void createNewRecord(String photoURL){
        // Add a new student record
        ContentValues values = new ContentValues();
        values.put(PhotosContentProvider.PHOTOURL, photoURL);

        Uri uri = getContext().getContentResolver().insert(
                PhotosContentProvider.CONTENT_URI, values);
    }

    private void removePhoto (PhotoDO photoDO) {
        String photoURL = photoDO.getURL();
        int result = getContext().getContentResolver().delete(PhotosContentProvider.CONTENT_URI, "photoURL=?", new String[]{photoURL});
        if (result == 0){
            Log.e("VT", "error deleting photo");
        }
    }

}
