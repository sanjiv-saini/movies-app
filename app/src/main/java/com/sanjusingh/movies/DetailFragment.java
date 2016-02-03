package com.sanjusingh.movies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanjusingh.movies.db.MoviesContract;
import com.sanjusingh.movies.retrofit.ApiService;
import com.sanjusingh.movies.retrofit.RestClient;
import com.sanjusingh.movies.retrofit.model.Data;
import com.sanjusingh.movies.retrofit.model.Movie;
import com.sanjusingh.movies.retrofit.model.Reviews;
import com.sanjusingh.movies.retrofit.model.Trailers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * Created by sanju singh on 12/28/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private LayoutInflater myInflater;
    private View rootView;
    private ApiService apiService = null;
    private Context context;
    private ArrayList<Trailers> trailersList = null;
    private ArrayList<Reviews> reviewsList = null;
    private final int Check_loader = 1;
    private String favTag = null;


    private Movie selectedMovie = null;
    public static final String DETAIL_KEY = "myMovie";


    public DetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(trailersList != null){
            outState.putParcelableArrayList("trailers", trailersList);
        }
        if(reviewsList != null){
            outState.putParcelableArrayList("reviews", reviewsList);
        }

        outState.putString("favButtonTag", favTag);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("trailers"))
                trailersList = savedInstanceState.getParcelableArrayList("trailers");

            if(savedInstanceState.containsKey("reviews"))
                reviewsList = savedInstanceState.getParcelableArrayList("reviews");

            if(savedInstanceState.containsKey("favButtonTag"))
                favTag = savedInstanceState.getString("favButtonTag");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String imageBaseUrl = "http://image.tmdb.org/t/p/";
        myInflater = inflater;
        context = getActivity();
        rootView = null;

        Bundle arguments = getArguments();
        if(arguments !=null){
            selectedMovie = arguments.getParcelable(DETAIL_KEY);
        }

        if(selectedMovie != null){

            rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            apiService = RestClient.getApiService();

            if(trailersList != null){
                displayTrailers();
            }else{
                fetchTrailers(selectedMovie.getId());
            }

            if(reviewsList !=null){
                displayReviews();
            }else{
                fetchReviews(selectedMovie.getId());
            }

            TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
            TextView releaseDateView = (TextView) rootView.findViewById(R.id.releaseDateView);
            TextView ratingView = (TextView) rootView.findViewById(R.id.ratingView);
            TextView overview = (TextView) rootView.findViewById(R.id.overviewField);
            ImageView backdropView = (ImageView) rootView.findViewById(R.id.backdropView);



            if(selectedMovie.getBackdrop_path() != null){
                String backdropUrl = imageBaseUrl + "w342/" + selectedMovie.getBackdrop_path();
                Picasso.with(getActivity()).load(backdropUrl).placeholder(R.drawable.backdrop).into(backdropView);
            }


            titleView.setText(selectedMovie.getTitle());
            ratingView.setText(selectedMovie.getVote_average().toString());
            releaseDateView.setText(selectedMovie.getRelease_date());
            overview.setText(selectedMovie.getOverview());

            final ImageView favButton = (ImageView) rootView.findViewById(R.id.favouriteButton);

            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favButton.setEnabled(false);
                    favButtonHandler(favButton);
                    favButton.setEnabled(true);
                }
            });

            //share first trailer url through intent
            final ImageView shareButton = (ImageView) rootView.findViewById(R.id.shareButton);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(trailersList != null && trailersList.size() > 0){
                        startActivity(Intent.createChooser(createShareVideoIntent(), "Share via"));
                    }else{
                        Toast.makeText(context,"No Trailer To Share", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(favTag != null){
            ImageView favButton = (ImageView) rootView.findViewById(R.id.favouriteButton);
            if(favTag.equals("off")){
                favButton.setImageResource(R.drawable.favourite_off);
                favButton.setTag(favTag);
            }else{
                favButton.setImageResource(R.drawable.favourite_on);
                favButton.setTag(favTag);
            }
        }else if(selectedMovie!=null){
            getLoaderManager().initLoader(Check_loader, null, this);
        }
    }

    private void fetchReviews(Long movieId){

        Call<Data<Reviews>> call = apiService.getReviews(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        final TextView statusText = (TextView) rootView.findViewById(R.id.reviewStatus);


        call.enqueue(new Callback<Data<Reviews>>() {

            @Override
              public void onResponse(retrofit.Response<Data<Reviews>> response, Retrofit retrofit) {
                Data<Reviews> data = response.body();
                if(data != null){
                    reviewsList = data.getResults();
                    if(reviewsList.size() > 0){
                        displayReviews();
                    } else{
                        statusText.setText("No Reviews");
                    }


                } else{
                    statusText.setText("ERROR");
                }

            }

            @Override
            public void onFailure(Throwable t) {
                statusText.setText("Call to server failed");
                Log.e(LOG_TAG, "Call to server failed: " + t.getMessage());
            }
        });
    }

    private void fetchTrailers(Long movieId){

        Call<Data<Trailers>> call = apiService.getTrailers(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        final TextView statusText = (TextView) rootView.findViewById(R.id.trailerStatus);

        call.enqueue(new Callback<Data<Trailers>>() {
            @Override
            public void onResponse(retrofit.Response<Data<Trailers>> response, Retrofit retrofit) {
                Data<Trailers> data = response.body();
                if(data != null){
                    trailersList = data.getResults();
                    if(trailersList.size() > 0){
                        displayTrailers();
                    } else{
                        statusText.setText("No Trailers");
                    }
                } else{
                    statusText.setText("ERROR");
                    Log.d(LOG_TAG, "Problem in parsing JSON");
                }

            }

            @Override
            public void onFailure(Throwable t) {
                statusText.setText("Call to server failed");
                Log.e(LOG_TAG, "Call to server failed: " + t.getMessage());
            }
        });
    }

    private Intent createShareVideoIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        String id = trailersList.get(0).getKey();
        intent.putExtra(intent.EXTRA_TEXT, "http://www.youtube.com/watch?v="+id);

        return intent;
    }

    private void displayReviews(){
        if(reviewsList != null) {
            LinearLayout reviewLayout = (LinearLayout) rootView.findViewById(R.id.reviewLayout);
            for (Reviews review : reviewsList) {
                View reviewItem = myInflater.inflate(R.layout.review_list_item, null);
                TextView authorNameText = (TextView) reviewItem.findViewById(R.id.authorNameText);
                TextView reviewText = (TextView) reviewItem.findViewById(R.id.reviewText);

                authorNameText.setText(review.getAuthor() + ":");
                reviewText.setText(review.getContent());

                reviewLayout.addView(reviewItem);
            }
        }
    }

    private void displayTrailers(){
        if(trailersList != null){
            String baseUrl = "http://img.youtube.com/vi/";
            String url;
            LinearLayout trailerLayout = (LinearLayout) rootView.findViewById(R.id.trailerLayout);

            for(final Trailers trailer : trailersList){
                url = baseUrl + trailer.getKey() + "/1.jpg";
                View trailerItem = myInflater.inflate(R.layout.trailer_item, null);
                ImageView trailerImage = (ImageView) trailerItem.findViewById(R.id.trailerImage);
                TextView trailerTitle = (TextView) trailerItem.findViewById(R.id.trailerTitle);
                trailerTitle.setText(trailer.getName());
                trailerImage.setAdjustViewBounds(true);
                Picasso.with(context).load(url).into(trailerImage);

                trailerItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        watchYoutubeVideo(trailer.getKey());
                    }
                });

                trailerLayout.addView(trailerItem);
            }

        }
    }

    private void watchYoutubeVideo(String id){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+id));
            startActivity(intent);
        }
    }


    private void favButtonHandler(ImageView favButton){

        if(favTag.equals("off")){

            // run query in async thread
            ContentValues movieValues = new ContentValues();

            movieValues.put(MoviesContract.MovieEntry._ID, selectedMovie.getId());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, selectedMovie.getTitle());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, selectedMovie.getRelease_date());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, selectedMovie.getVote_average());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, selectedMovie.getOverview());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, selectedMovie.getPoster_path());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, selectedMovie.getBackdrop_path());

            getActivity().getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movieValues);
            favTag = "on";
            favButton.setTag(favTag);
            favButton.setImageResource(R.drawable.favourite_on);
        } else{
            //run query in async thread
            String id = selectedMovie.getId().toString();
            getActivity().getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,
                    MoviesContract.MovieEntry._ID + "= ?",
                    new String[]{id});
            favTag = "off";
            favButton.setImageResource(R.drawable.favourite_off);
            favButton.setTag(favTag);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String movieId = selectedMovie.getId().toString();

        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                MoviesContract.MovieEntry._ID + "= ?",
                new String[]{movieId},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ImageView favButton = (ImageView) rootView.findViewById(R.id.favouriteButton);
        if(data.moveToNext()){
            favButton.setImageResource(R.drawable.favourite_on);
            favTag = "on";
            favButton.setTag(favTag);
        }else{
            favButton.setImageResource(R.drawable.favourite_off);
            favTag = "off";
            favButton.setTag(favTag);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
