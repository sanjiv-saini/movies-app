package com.sanjusingh.movies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sanju singh on 12/11/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<String> dataSet;
    public ImageAdapter(Context c, List<String> dataSet) {
        super();
        this.context = c;
        this.dataSet = dataSet;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
