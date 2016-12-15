package com.udacity.jeremywright.virtualtraveler.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.jeremywright.virtualtraveler.R;
import com.udacity.jeremywright.virtualtraveler.dataobjects.PhotoDO;

import java.util.List;

/**
 * Created by itg5796 on 12/13/16.
 */

public class PhotoGridAdapter extends ArrayAdapter<PhotoDO> {


    public PhotoGridAdapter(Context context, List<PhotoDO> photoDOs) {
        super(context, 0, photoDOs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PhotoDO photo = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_grid_item, parent, false);
        }

        ImageView photoImageView = (ImageView)convertView.findViewById(R.id.grid_image_view);
        Log.v("photo", photo.getURL());
        Picasso.with(getContext()).load(photo.getURL()).into(photoImageView);

        photoImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Note: I was originally going to make this a double tap action, but that's against UI guidelines
                //TODO: add to favorites
                Toast.makeText(getContext(),"Added to favorites", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return convertView;
    }
}
