package com.sanjusingh.movies.retrofit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sanju singh on 12/30/2015.
 */
public class Trailers implements Parcelable{
    private String name;
    private String key;

    public Trailers() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Trailers(Parcel parcel){
        this.name = parcel.readString();
        this.key = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(key);
    }

    public static final Creator<Trailers> CREATOR = new Creator<Trailers>(){
        @Override
        public Trailers createFromParcel(Parcel source) {
            return new Trailers(source);
        }

        @Override
        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };
}
