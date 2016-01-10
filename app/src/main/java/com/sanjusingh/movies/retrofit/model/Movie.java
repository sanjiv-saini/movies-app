package com.sanjusingh.movies.retrofit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sanju singh on 12/11/2015.
 */
public class Movie implements Parcelable {
    private Long id;
    private String title;
    private String overview;
    private String poster_path;
    private String release_date;
    private Double vote_average;
    private String backdrop_path;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setVote_average(Double vote_average) { this.vote_average = vote_average;  }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }


    public Movie(Parcel parcel) {
        this.id = parcel.readLong();
        this.title = parcel.readString();
        this.overview = parcel.readString();
        this.release_date = parcel.readString();
        this.vote_average = parcel.readDouble();
        this.poster_path = parcel.readString();
        this.backdrop_path = parcel.readString();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeDouble(vote_average);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
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
