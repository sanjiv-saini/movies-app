package com.sanjusingh.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sanjusingh.movies.retrofit.ApiService;
import com.sanjusingh.movies.retrofit.RestClient;
import com.sanjusingh.movies.retrofit.model.Data;
import com.sanjusingh.movies.retrofit.model.Movie;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by sanju singh on 12/11/2015.
 */
public class PosterFragment extends Fragment {

    private final String LOG_TAG= PosterFragment.class.getSimpleName();
    private ImageAdapter imageAdapter = null;
    private ArrayList<Movie> movieList = null;
    private String sortCriteria = "popularity.desc";

    public PosterFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            sortCriteria = savedInstanceState.getString("sortType");

            if(savedInstanceState.containsKey("movies"))
                movieList = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if(movieList != null) {
            imageAdapter = new ImageAdapter(getActivity(), movieList);
        } else{
            imageAdapter = new ImageAdapter(getActivity(), new ArrayList<Movie>());
        }

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = imageAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movie", selectedMovie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String  moviesOrder = prefs.getString(getString(R.string.pref_sort_criteria_key), getString(R.string.pref_criteria_most_popular));

        if(moviesOrder.equals("favourites")){
           // showFavouriteMovies();
        } else if(isConnected()) {
            if (!sortCriteria.equals(moviesOrder)) {
                sortCriteria = moviesOrder;
                imageAdapter.clear();
                updateMovies();
            } else if (movieList == null) {
                updateMovies();
            }
        } else{
            Toast toast = Toast.makeText(getActivity()," Check you connection and try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(movieList != null)
        {
            outState.putParcelableArrayList("movies", movieList);
        }
        outState.putString("sortType", sortCriteria);
        super.onSaveInstanceState(outState);
    }


    public void updateMovies(){

        ApiService apiService = RestClient.getApiService();
        Call<Data<Movie>> call = apiService.getMovies(sortCriteria, BuildConfig.THE_MOVIE_DB_API_KEY);

        call.enqueue(new Callback<Data<Movie>>() {
            @Override
            public void onResponse(Response<Data<Movie>> response, Retrofit retrofit) {
                Data<Movie> data = response.body();
                if(data != null){
                    if(data.getResults().size() > 0){
                        movieList = data.getResults();
                        imageAdapter.addAll(movieList);
                    } else{
                        Log.v(LOG_TAG, "No Movies");
                    }


                } else{
                    try {
                        String str = response.errorBody().string();
                        Log.d(LOG_TAG, "Problem in parsing JSON" + str);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.v(LOG_TAG, t.getMessage() +"  ");
                t.printStackTrace();
            }
        });
    }
}
