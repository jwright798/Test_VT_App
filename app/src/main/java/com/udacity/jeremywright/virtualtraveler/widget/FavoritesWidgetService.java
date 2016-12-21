package com.udacity.jeremywright.virtualtraveler.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;
import com.udacity.jeremywright.virtualtraveler.R;
import com.udacity.jeremywright.virtualtraveler.contentprovider.PhotosContentProvider;
import com.udacity.jeremywright.virtualtraveler.dataobjects.PhotoDO;

import java.io.IOException;

/**
 * Created by Jeremy Wright on 12/20/16.
 */

//Followed template from http://docs.huihoo.com/android/3.0/resources/samples/StackWidget/index.html
public class FavoritesWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext(), intent, FavoritesWidgetService.this.getContentResolver());
    }


}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int defaultCount = 0;
    private Cursor mCursor;
    private ContentResolver mContentResolver;

    public GridRemoteViewsFactory(Context context, Intent intent, ContentResolver resolver) {
        mContext = context;
        mContentResolver = resolver;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        // We sleep for 2 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataSetChanged() {
        //close the existing cursor if it exists
        if(mCursor != null){
            mCursor.close();
        }

        //get new data from the favorites provider
        mCursor = mContentResolver.query(PhotosContentProvider.CONTENT_URI, null, null, null, null);

    }

    @Override
    public void onDestroy() {

        //Kill the cursor
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mCursor !=null) {
            return mCursor.getCount();
        }
        return defaultCount;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }
        //move the cursor to the correct location
        final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.photo_grid_item);
        PhotoDO photo = new PhotoDO();
        if (mCursor.moveToPosition(position)){
            photo = new PhotoDO(mCursor.getString(mCursor.getColumnIndex(PhotosContentProvider.PHOTOURL)));
        } else {
            Log.e("Widget", "unable to get photo object from Cursor");
        }

        //Thanks to the forums for showing me how to use Picasso with the widget
        try {
            Bitmap image = Picasso.with(mContext).load(photo.getURL()).get();
            remoteViews.setImageViewBitmap(R.id.grid_image_view, image);
        } catch (IOException e){
            Log.e("Widget", e.getMessage());
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
