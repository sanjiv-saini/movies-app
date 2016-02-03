package com.sanjusingh.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.sanjusingh.movies.db.MoviesContract;
import com.sanjusingh.movies.retrofit.ApiService;
import com.sanjusingh.movies.retrofit.RestClient;
import com.sanjusingh.movies.retrofit.model.Data;
import com.sanjusingh.movies.retrofit.model.Movie;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
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
    private int mPosition = 0;
    private static final String POSITION_KEY = "moviePosition";
    private static final String PREF_KEY = "moviePref";
    private GridView gridView;
    private String moviePref  = "popularity.desc";

    private Handler handler = new Handler()  {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                ((PosterFragment.Callback) getActivity()).showTwoPaneMovieDetail(null);
            }else if(msg.what == 2) {
                ((PosterFragment.Callback) getActivity()).showTwoPaneMovieDetail(movieList.get(0));
            }
        }
    };

    public interface Callback{
        public void onItemSelected(Movie selectMovie);
        public void showTwoPaneMovieDetail(Movie movie);
    }

    public PosterFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("movies"))
                movieList = savedInstanceState.getParcelableArrayList("movies");
            if(savedInstanceState.containsKey(POSITION_KEY))
                mPosition = savedInstanceState.getInt(POSITION_KEY);
            if(savedInstanceState.containsKey(PREF_KEY))
                moviePref = savedInstanceState.getString(PREF_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String moviesOrder = prefs.getString(getString(R.string.pref_sort_criteria_key),
                getString(R.string.pref_criteria_most_popular));

        if(!moviesOrder.equals(moviePref)){
            movieList = null;
            mPosition = 0;
        }

        if(movieList != null) {
            imageAdapter = new ImageAdapter(getActivity(), movieList);

        } else{
            imageAdapter = new ImageAdapter(getActivity(), new ArrayList<Movie>());
        }

        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = imageAdapter.getItem(position);
                mPosition = position;
                ((Callback) getActivity())
                        .onItemSelected(selectedMovie);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(movieList != null) {
            if(MainActivity.mTwoPane){
                gridView.setSelection(mPosition);
                ((Callback)getActivity()).showTwoPaneMovieDetail(movieList.get(mPosition));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String moviesOrder = prefs.getString(getString(R.string.pref_sort_criteria_key),
                getString(R.string.pref_criteria_most_popular));

        if(!moviesOrder.equals(moviePref)){
            movieList = null;
            mPosition = 0;
            imageAdapter.clear();
            if(MainActivity.mTwoPane){
                ((Callback)getActivity()).showTwoPaneMovieDetail(null);
            }
            moviePref = moviesOrder;
        }

        if(moviesOrder.equals("popularity.desc")){
            getActivity().setTitle("Movies | Most Popular");
        }else if(moviesOrder.equals("vote_average.desc")){
            getActivity().setTitle("Movies | Highest Rated");
        }else if(moviesOrder.equals("favourites")){
            getActivity().setTitle("Movies | Favourites");
        }


        if (movieList == null) {
            // Update Poster
            if (moviesOrder.equals("favourites")) {
                getLoaderManager().initLoader(Movie_loader, null, this);
            } else if(isConnected()) {
                getMovieFromApi(moviesOrder);
            } else{
                Toast.makeText(getActivity(), "No Network Connection", Toast.LENGTH_SHORT).show();
            }
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
        outState.putInt(POSITION_KEY, mPosition);
        outState.putString(PREF_KEY, moviePref);

        super.onSaveInstanceState(outState);
    }


    public void getMovieFromApi(String sortCriteria){

        ApiService apiService = RestClient.getApiService();
        Call<Data<Movie>> call = apiService.getMovies(sortCriteria, BuildConfig.THE_MOVIE_DB_API_KEY);

        call.enqueue(new retrofit.Callback<Data<Movie>>() {
            @Override
            public void onResponse(Response<Data<Movie>> response, Retrofit retrofit) {
                Data<Movie> data = response.body();
                if(data != null){
                    if(data.getResults().size() > 0){
                        movieList = data.getResults();
                        //show first movie details in detail fragment
                        if(MainActivity.mTwoPane) {
                            ((Callback) getActivity()).showTwoPaneMovieDetail(movieList.get(0));
                        }

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String moviesOrder = prefs.getString(getString(R.string.pref_sort_criteria_key),
                getString(R.string.pref_criteria_most_popular));


        if (moviesOrder.equals("favourites")){

            imageAdapter.clear();
            mPosition = 0;
            if(MainActivity.mTwoPane){
                handler.sendEmptyMessage(1);
            }

            if(data.moveToFirst()){

                movieList= new ArrayList<Movie>();

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

                imageAdapter.addAll(movieList);

                if (MainActivity.mTwoPane){
                    handler.sendEmptyMessage(2);
                }

            } else{
                Toast.makeText(getActivity(), "No Favourite Movies",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
