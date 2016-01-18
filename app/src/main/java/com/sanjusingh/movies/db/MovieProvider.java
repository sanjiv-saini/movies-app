package com.sanjusingh.movies.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by sanju singh on 1/15/2016.
 */
public class MovieProvider extends ContentProvider{

    private MovieDbHelper openHelper;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int MOVIE = 1;

    @Override
    public boolean onCreate() {
        openHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        final int match = uriMatcher.match(uri);

        switch (match){

            case MOVIE:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)){
            case MOVIE:
            {
                retCursor = openHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIE:
            {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if(_id >0)
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowDeleted;

        if(selection == null) selection = "1";
        switch (match){
            case MOVIE:
            {
                rowDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        db.close();
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowUpdated;

        switch (match) {
            case MOVIE:
                rowUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        db.close();
        return rowUpdated;
    }

    static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIE , MOVIE);

        return matcher;
    }
}
