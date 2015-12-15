package com.sanjusingh.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

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
        if(savedInstanceState != null && savedInstanceState.containsKey("movies")){
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
        String  moviesOrder= prefs.getString(getString(R.string.pref_sort_criteria_key), getString(R.string.pref_criteria_most_popular));

        if(movieList == null) {
            updateMovies();
        } else if(!sortCriteria.equals(moviesOrder)){
            sortCriteria = moviesOrder;
            imageAdapter.clear();
            updateMovies();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(movieList != null)
        {
            outState.putParcelableArrayList("movies", movieList);
        }
        super.onSaveInstanceState(outState);
    }


    public void updateMovies(){

        FetchMovieDBTask fetchMovieDBTask = new FetchMovieDBTask();
        fetchMovieDBTask.execute();

    }

    private class FetchMovieDBTask extends AsyncTask<Void, Void, Movie[]>{

        @Override
        protected Movie[] doInBackground(Void... params) {

            final String BaseUrl = "http://api.themoviedb.org/3/discover/movie?";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSONStr = null;

            try{

                Uri uri = Uri.parse(BaseUrl).buildUpon()
                        .appendQueryParameter("sort_by", sortCriteria)
                        .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuffer buffer = new StringBuffer("");
                while((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    return null;
                }

                JSONStr = buffer.toString();

            } catch(IOException e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing Stream", e);
                    }
                }

            }

            return getMoviesFromJSON(JSONStr);

        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if(result != null){
                movieList = new ArrayList<Movie>(Arrays.asList(result));
                imageAdapter.addAll(movieList);
            }
        }

        private Movie[] getMoviesFromJSON(String JSONStr){

            final String BASE_URL = "http://image.tmdb.org/t/p/w185";
            final String MOVIES_RESULT = "results";
            final String POSTER_PATH = "poster_path";
            final String TITLE = "original_title";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String USER_RATING = "vote_average";
            Movie[] movies = null;

            try {
                JSONObject moviesObject = new JSONObject(JSONStr);
                JSONArray resultArray = moviesObject.getJSONArray(MOVIES_RESULT);

                movies = new Movie[resultArray.length()];

                for(int i=0; i < resultArray.length(); i++){

                    JSONObject movieInfo = resultArray.getJSONObject(i);
                    //posterLinks[i] = BASE_URL + movieInfo.getString(POSTER_PATH);
                    movies[i] = new Movie(
                            movieInfo.getString(TITLE),
                            movieInfo.getString(OVERVIEW),
                            movieInfo.getString(RELEASE_DATE),
                            movieInfo.getDouble(USER_RATING),
                            BASE_URL + movieInfo.getString(POSTER_PATH)
                    );
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return movies;
        }

    }
}
