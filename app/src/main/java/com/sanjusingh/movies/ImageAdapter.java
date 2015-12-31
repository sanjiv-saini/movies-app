package com.sanjusingh.movies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sanjusingh.movies.retrofit.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sanju singh on 12/11/2015.
 */
public class ImageAdapter extends ArrayAdapter<Movie> {
    private Context context;

    public ImageAdapter(Context c, List<Movie> dataSet) {
        super(c,0,dataSet);
        context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String imageBaseUrl = "http://image.tmdb.org/t/p/";

        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(context);
            view.setAdjustViewBounds(true);
        }

        Movie movie = getItem(position);
        String url = imageBaseUrl + "w185/" + movie.getPoster_path();

        Picasso.with(context).load(url).placeholder(R.drawable.placeholder).error(R.drawable.error).into(view);
        return view;
    }
}
