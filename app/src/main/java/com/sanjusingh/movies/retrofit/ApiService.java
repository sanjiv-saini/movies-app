package com.sanjusingh.movies.retrofit;

import com.sanjusingh.movies.retrofit.model.Data;
import com.sanjusingh.movies.retrofit.model.Movie;
import com.sanjusingh.movies.retrofit.model.Reviews;
import com.sanjusingh.movies.retrofit.model.Trailers;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by sanju singh on 12/27/2015.
 */

public interface ApiService {

    @GET("movie/{id}/reviews")
    Call<Data<Reviews>> getReviews(@Path("id") Long movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<Data<Trailers>> getTrailers(@Path("id") Long movieId, @Query("api_key") String apiKey);

    @GET("discover/movie")
    Call<Data<Movie>> getMovies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey);
}
