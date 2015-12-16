package com.sanjusingh.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Movie selectedMovie = null;
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if((intent != null) && intent.hasExtra("movie")){
                selectedMovie = intent.getExtras().getParcelable("movie");
            }

            TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
            TextView releaseDateView = (TextView) rootView.findViewById(R.id.releaseDateView);
            TextView ratingView = (TextView) rootView.findViewById(R.id.ratingView);
            TextView overview = (TextView) rootView.findViewById(R.id.overviewField);
            ImageView thumbnailView = (ImageView) rootView.findViewById(R.id.thumbnailView);
            thumbnailView.setAdjustViewBounds(true);

            if(selectedMovie != null){
                titleView.setText(selectedMovie.getTitle());
                ratingView.setText(selectedMovie.getUserRating().toString());
                releaseDateView.setText(selectedMovie.getReleaseDate());
                overview.setText("OVERVIEW:\n" + selectedMovie.getOverview());
                Picasso.with(getActivity()).load(selectedMovie.getPosterUrl()).placeholder(R.drawable.placeholder).error(R.drawable.error).into(thumbnailView);
            }

            return rootView;
        }
    }
}
