package com.udacity.jeremywright.virtualtraveler.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jeremywright on 12/14/16.
 */

public class PhotoDO implements Parcelable {
    private String id;
    private String owner;
    private String secret;
    private String server;
    private int farm;
    private String title;
    private int ispublic;
    private int isfriend;
    private int isfamily;
    private String url;



    public PhotoDO(Parcel in){
        this.id = in.readString();
        this.owner = in.readString();
        this.secret = in.readString();
        this.server = in.readString();
        this.farm = in.readInt();
        this.title = in.readString();
        this.ispublic = in.readInt();
        this.isfriend = in.readInt();
        this.isfamily = in.readInt();
        this.url = in.readString();
    }

    public PhotoDO(String url) {
        this.url = url;
    }

    public PhotoDO(JSONObject object) throws JSONException {
        this.id = object.getString("id");
        this.owner = object.getString("owner");
        this.secret = object.getString("secret");
        this.server = object.getString("server");
        this.farm = object.getInt("farm");
        this.title = object.getString("title");
        this.ispublic = object.getInt("ispublic");
        this.isfriend = object.getInt("isfriend");
        this.isfamily = object.getInt("isfamily");
        this.url = getURL();
    }

    public PhotoDO(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getFarm() {
        return farm;
    }

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIspublic() {
        return ispublic;
    }

    public void setIspublic(int ispublic) {
        this.ispublic = ispublic;
    }

    public int getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(int isfriend) {
        this.isfriend = isfriend;
    }

    public int getIsfamily() {
        return isfamily;
    }

    public void setIsfamily(int isfamily) {
        this.isfamily = isfamily;
    }

    public String getURL(){
        if (url != null && !url.isEmpty()){
            return url;
        } else {
            return "https://farm" + Integer.toString(farm) + ".staticflickr.com/" + server + "/" + id + "_" + secret + "_s.jpg";
        }
    }

    public void setURL(String url){
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<PhotoDO> CREATOR
            = new Parcelable.Creator<PhotoDO>() {
        public PhotoDO createFromParcel(Parcel in){
            return new PhotoDO(in);
        }

        public PhotoDO[] newArray(int size) {
            return new PhotoDO[size];
        }
    };
}
