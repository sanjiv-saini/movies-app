package com.sanjusingh.movies.retrofit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sanju singh on 12/27/2015.
 */
public class Reviews implements Parcelable{
    private String author;
    private String content;

    public Reviews(){}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Reviews(Parcel parcel){
        this.author = parcel.readString();
        this.content = parcel.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>(){

        @Override
        public Reviews createFromParcel(Parcel source) {
            return new Reviews(source);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };
}
