package com.sanjusingh.movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sanju singh on 12/11/2015.
 */
public class Movie implements Parcelable{
    private String title;
    private String overview;
    private String posterUrl;
    private String releaseDate;
    private Double userRating;

    public Movie(String title, String overview, String releaseDate, Double userRating, String posterUrl){
        this.title= title;
        this.overview = overview;
        this.posterUrl = posterUrl;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public Movie(Parcel parcel) {
        this.title = parcel.readString();
        this.overview = parcel.readString();
        this.releaseDate = parcel.readString();
        this.userRating = parcel.readDouble();
        this.posterUrl = parcel.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public Double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeDouble(userRating);
        dest.writeString(posterUrl);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }
    };


}
