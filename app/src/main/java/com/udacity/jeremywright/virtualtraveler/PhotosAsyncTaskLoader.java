package com.udacity.jeremywright.virtualtraveler;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.udacity.jeremywright.virtualtraveler.dataobjects.PhotoDO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jeremywright on 12/17/16.
 */

//Using this as a combination of an AsyncTask and Loader

public class PhotosAsyncTaskLoader extends AsyncTaskLoader<ArrayList<PhotoDO>>{

    private String latitude;
    private String longitude;

    public PhotosAsyncTaskLoader(Context context) {
        super(context);
    }

    public PhotosAsyncTaskLoader(Context context, String lat, String longitude){
        super(context);
        this.latitude = lat;
        this.longitude = longitude;
    }

    @Override
    public ArrayList<PhotoDO> loadInBackground() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String photoJSONString = null;

        try{
            String BASE_URL = getContext().getString(R.string.flickr_base_url);
            
            String apiKey = getContext().getString(R.string.flickr_api_key);

            if (apiKey.isEmpty()){
                Toast.makeText(getContext(), R.string.empty_api_key_text, Toast.LENGTH_SHORT).show();
            }

            String photoSearchUrl = BASE_URL+"&api_key="+ apiKey+ "&lat="+latitude+"&lon="+longitude;
            Log.v("ATL", "url:"+photoSearchUrl);

            URL url = new URL(photoSearchUrl);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the input stream
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));


            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            photoJSONString = buffer.toString();

        } catch (IOException e){
            Log.e("ATL", "Error ", e);
            return null;
        } finally {
            if (urlConnection !=null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (final IOException e){
                    Log.e("ATL", "Error closing stream", e);
                }
            }
        }

        try{
            return getPhotosFromJSON(photoJSONString);
        } catch (JSONException e){
            Log.e("ATL", e.getMessage(),e);
        }

        return null;
    }

    //Rather than a bunch of nested null checks, throwing a JSONException and returning null is the best option
    private ArrayList<PhotoDO> getPhotosFromJSON(String jsonString) throws  JSONException{
        ArrayList<PhotoDO> photoList = new ArrayList<PhotoDO>();
        JSONObject objectJSON = new JSONObject(jsonString);
        JSONObject photoJSON = objectJSON.getJSONObject(getContext().getString(R.string.photos_key));
        JSONArray photoArray = photoJSON.getJSONArray(getContext().getString(R.string.photo_key));

        //populate the arraylist
        for (int i = 0; i<photoArray.length(); i++){
            JSONObject photoObject = photoArray.getJSONObject(i);
            PhotoDO photo = new PhotoDO(photoObject);
            photoList.add(photo);
        }
        return photoList;
    }


}
