package com.sanjusingh.movies.retrofit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by sanju singh on 12/28/2015.
 */

public class RestClient {
    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static ApiService apiService;

    private RestClient(){}

    static{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getApiService()
    {
        return apiService;
    }
}
