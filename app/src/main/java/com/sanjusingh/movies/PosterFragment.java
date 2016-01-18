package com.sanjusingh.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sanjusingh.movies.db.MoviesContract;
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
public class PosterFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG= PosterFragment.class.getSimpleName();
    private ImageAdapter imageAdapter = null;
    private ArrayList<Movie> movieList = null;
    private final int Movie_loader = 0;
    public static boolean prefChanged = false; // modified from settingsActivity

    public PosterFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
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
        String moviesOrder = prefs.getString(getString(R.string.pref_sort_criteria_key),
                getString(R.string.pref_criteria_most_popular));

        if (movieList == null || prefChanged) {

            if (!isConnected() || moviesOrder.equals("favourites")) {
                getLoaderManager().initLoader(Movie_loader, null, this);
            } else {
                getMovieFromApi(moviesOrder);
            }

            prefChanged = false;
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
        super.onSaveInstanceState(outState);
    }


    public void getMovieFromApi(String sortCriteria){

        ApiService apiService = RestClient.getApiService();
        Call<Data<Movie>> call = apiService.getMovies(sortCriteria, BuildConfig.THE_MOVIE_DB_API_KEY);

        call.enqueue(new Callback<Data<Movie>>() {
            @Override
            public void onResponse(Response<Data<Movie>> response, Retrofit retrofit) {
                Data<Movie> data = response.body();
                if(data != null){
                    if(data.getResults().size() > 0){
                        movieList = data.getResults();
                        imageAdapter.clear();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = {
                MoviesContract.MovieEntry._ID,
                MoviesContract.MovieEntry.COLUMN_TITLE,
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                MoviesContract.MovieEntry.COLUMN_OVERVIEW,
                MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
                MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH
        };

        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                projections,
                null,
                null,
                null
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int _ID = 0;
        int COL_TITLE = 1;
        int COL_RELEASE_DATE = 2;
        int COL_VOTE_AVERAGE = 3;
        int COL_OVERVIEW = 4;
        int COL_POSTER_PATH = 5;
        int COL_BACKDROP_PATH = 6;

        if(data.moveToFirst()){
            movieList = new ArrayList<Movie>();

            do{
                Movie movie = new Movie();
                movie.setId(data.getLong(_ID));
                movie.setTitle(data.getString(COL_TITLE));
                movie.setRelease_date(data.getString(COL_RELEASE_DATE));
                movie.setVote_average(data.getDouble(COL_VOTE_AVERAGE));
                movie.setOverview(data.getString(COL_OVERVIEW));
                movie.setPoster_path(data.getString(COL_POSTER_PATH));
                movie.setBackdrop_path(data.getString(COL_BACKDROP_PATH));

                movieList.add(movie);
            }while(data.moveToNext());

            imageAdapter.clear();
            imageAdapter.addAll(movieList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
