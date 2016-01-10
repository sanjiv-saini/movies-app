package com.sanjusingh.movies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanjusingh.movies.retrofit.ApiService;
import com.sanjusingh.movies.retrofit.RestClient;
import com.sanjusingh.movies.retrofit.model.Data;
import com.sanjusingh.movies.retrofit.model.Movie;
import com.sanjusingh.movies.retrofit.model.Reviews;
import com.sanjusingh.movies.retrofit.model.Trailers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * Created by sanju singh on 12/28/2015.
 */
public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private OkHttpClient client = new OkHttpClient();
    private LayoutInflater myInflater;
    private View rootView;
    private ApiService apiService = null;
    private Context context;
    private List<Trailers> trailersList;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Movie selectedMovie = null;
        String imageBaseUrl = "http://image.tmdb.org/t/p/";
        myInflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        context = getActivity();

        Intent intent = getActivity().getIntent();
        if((intent != null) && intent.hasExtra("movie")){
            selectedMovie = intent.getExtras().getParcelable("movie");
        }

        apiService = RestClient.getApiService();
        fetchReviews(selectedMovie.getId());
        fetchTrailers(selectedMovie.getId());

        TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.releaseDateView);
        TextView ratingView = (TextView) rootView.findViewById(R.id.ratingView);
        TextView overview = (TextView) rootView.findViewById(R.id.overviewField);
        ImageView backdropView = (ImageView) rootView.findViewById(R.id.backdropView);

        String backdropUrl = imageBaseUrl + "w342/" + selectedMovie.getBackdrop_path();

        if(selectedMovie != null){
            titleView.setText(selectedMovie.getTitle());
            ratingView.setText(selectedMovie.getVote_average().toString());
            releaseDateView.setText(selectedMovie.getRelease_date());
            overview.setText(selectedMovie.getOverview());
            Picasso.with(getActivity()).load(backdropUrl).into(backdropView);
        }

        final ImageView favButton = (ImageView) rootView.findViewById(R.id.favouriteButton);
        favButton.setTag("off");
        favButton.setImageResource(R.drawable.favourite_off);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favButtonHandler(favButton);
            }
        });

        //share first trailer url through intent
        final ImageView shareButton = (ImageView) rootView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(createShareVideoIntent(), "Share via"));
            }
        });


        return rootView;

    }

    private void fetchReviews(Long movieId){

        Call<Data<Reviews>> call = apiService.getReviews(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        final TextView statusText = (TextView) rootView.findViewById(R.id.reviewStatus);


        call.enqueue(new Callback<Data<Reviews>>() {

            @Override
              public void onResponse(retrofit.Response<Data<Reviews>> response, Retrofit retrofit) {
                Data<Reviews> data = response.body();
                List<Reviews> reviewsList = null;
                if(data != null){
                    reviewsList = data.getResults();
                    if(reviewsList.size() > 0){
                        displayReviews(reviewsList);
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

    private void displayReviews(List<Reviews> reviewsList){
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
        String tag = (String) favButton.getTag();
        if(tag.equals("off")){
            favButton.setImageResource(R.drawable.favourite_on);
            favButton.setTag("on");
        } else{
            favButton.setImageResource(R.drawable.favourite_off);
            favButton.setTag("off");
        }
    }
}
