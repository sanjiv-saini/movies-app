package com.sanjusingh.movies;

/**
 * Created by sanju singh on 12/11/2015.
 */
public class Movie {
    private String title;
    private String overview;
    private String posterUrl;
    private String releaseDate;
    private double userRating;

    public Movie(String title, String overview, String releaseDate, double userRating, String posterUrl){
        this.title= title;
        this.overview = overview;
        this.posterUrl = posterUrl;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
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

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

}
