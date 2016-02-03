package com.sanjusingh.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sanjusingh.movies.retrofit.model.Movie;


public class MainActivity extends ActionBarActivity implements PosterFragment.Callback{

    public static boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_details_fragment) != null){
            mTwoPane = true;
         //   if(savedInstanceState == null){
           //     getSupportFragmentManager().beginTransaction()
             //           .replace(R.id.movie_details_fragment, new DetailFragment())
               //         .commit();
            //}
        }else{
            mTwoPane = false;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie selectedMovie) {
        if(mTwoPane){
            showTwoPaneMovieDetail(selectedMovie);
        } else{
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("movie", selectedMovie);
            startActivity(intent);
        }
    }

    @Override
    public void showTwoPaneMovieDetail(Movie movie){
        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.DETAIL_KEY, movie);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_details_fragment, fragment)
                .commit();
    }
}
