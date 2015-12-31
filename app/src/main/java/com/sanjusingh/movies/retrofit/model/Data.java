package com.sanjusingh.movies.retrofit.model;

import java.util.ArrayList;

/**
 * Created by sanju singh on 12/28/2015.
 */
public class Data<T> {
    private ArrayList<T> results;

    public ArrayList<T> getResults() {
        return results;
    }

    public void setResults(ArrayList<T> results) {
        this.results = results;
    }
}
