package com.udacity.jeremywright.virtualtraveler;

import android.os.AsyncTask;
import android.util.Log;

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
 * Created by itg5796 on 12/14/16.
 */

public class PhotoServiceHelper {

    private PhotoServiceDelegate photoServiceDelegate;
    private final String LOG_TAG = PhotoServiceHelper.class.getSimpleName();

    public void setPhotoServiceDelegate(PhotoServiceDelegate classname){
        photoServiceDelegate = classname;
    }

    //This call gets photos from Flickr
    public void getPhotos(double lat, double longitude) {
        if (photoServiceDelegate == null){
            Log.e(LOG_TAG, "delegate is null");
            return;
        }
        AsyncTask photosTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                String photoJSONString = null;

                try{
                    //TODO: convert to strings.xml
                    final String BASE_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1";
                    String latitude = (String) params[0];
                    String longitude = (String) params[1];
                    String apiKey = "292223080da15057b3f18bb40b3ae16b";

                    String photoSearchUrl = BASE_URL+"&api_key="+ apiKey+ "&lat="+latitude+"&lon="+longitude;
                    Log.v(LOG_TAG, "url:"+photoSearchUrl);

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
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                } finally {
                    if (urlConnection !=null){
                        urlConnection.disconnect();
                    }
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (final IOException e){
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try{
                    return getPhotosFromJSON(photoJSONString);
                } catch (JSONException e){
                    Log.e(LOG_TAG, e.getMessage(),e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object photoString) {
                if (photoServiceDelegate != null){
                    photoServiceDelegate.photosResponse((ArrayList<PhotoDO>) photoString);
                }
            }

            private ArrayList<PhotoDO> getPhotosFromJSON(String jsonString) throws  JSONException{
                ArrayList<PhotoDO> photoList = new ArrayList<PhotoDO>();
                JSONObject objectJSON = new JSONObject(jsonString);
                JSONObject photoJSON = objectJSON.getJSONObject("photos");
                JSONArray photoArray = photoJSON.getJSONArray("photo");

                //populate the arraylist
                for (int i = 0; i<photoArray.length(); i++){
                    JSONObject photoObject = photoArray.getJSONObject(i);
                    PhotoDO photo = new PhotoDO(photoObject);
                    photoList.add(photo);
                }
                return photoList;
            }
        };
        String[] latlong = new String[2];
        latlong[0] = Double.toString(lat);
        latlong[1] = Double.toString(longitude);
        photosTask.execute(latlong);
    }

    //Delegate for callbacks
    public interface PhotoServiceDelegate {
         void photosResponse(ArrayList<PhotoDO> photos);
    }

}
