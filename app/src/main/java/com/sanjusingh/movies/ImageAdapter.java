package com.sanjusingh.movies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sanju singh on 12/11/2015.
 */
public class ImageAdapter extends ArrayAdapter<String> {
    private Context context;

    public ImageAdapter(Context c, List<String> dataSet) {
        super(c,0,dataSet);
        context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(context);
        }

        String url = (String) getItem(position);

        Picasso.with(context).load(url).into(view);
        return view;
    }
}
